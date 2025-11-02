package com.hoppinzq.service.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.constant.AuthConstant;
import com.hoppinzq.dal.dao.UserMapper;
import com.hoppinzq.dal.po.UserPO;
import com.hoppinzq.property.QQProperty;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.exception.UserException;
import com.hoppinzq.service.user.UserService;
import com.hoppinzq.service.util.SnowflakeIdWorker;
import com.hoppinzq.util.HttpClientComm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import static com.hoppinzq.enums.LoginTypeEnum.LOGIN_QQ;
import static com.hoppinzq.service.user.UserService.userToken2RedisPrefix;

/**
 * @author: zq
 */
@ServiceRegister
@ApiServiceMapping(title = "qq第三方认证", description = "qq第三方认证")
public class QQOAuthServiceImpl implements QQOAuthService, Serializable {

    private static final long serialVersionUID = 2783377098145240357L;
    private static final String authorization_code = "authorization_code";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final Logger logger = LoggerFactory.getLogger(QQOAuthServiceImpl.class);
    @Resource
    private HttpClientComm httpClientComm;
    @Resource
    private QQProperty qqProperty;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;

    /**
     * 获取access_token跟refresh_token
     *
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     * @throws UserException
     */
    @ApiMapping(value = "qqToken", title = "获取qq访问令牌", description = "获取qq访问令牌")
    public String getAccessToken(String code) throws UnsupportedEncodingException, UserException {
        logger.info("开始获取access_token");
        String postUrl = AuthConstant.QQ_OAUTH_TOKEN_URL + "?grant_type=" + authorization_code +
                "&code=" + code +
                "&fmt=json" +
                "&need_openid=1" +
                "&client_id=" + qqProperty.getCilent_id() +
                "&redirect_uri=" + qqProperty.getReurl() +
                "&client_secret=" + qqProperty.getClient_secret();
        logger.info("请求的qq的url是:" + postUrl);
        String giteeRes = httpClientComm.post(postUrl);
        logger.info("响应是:" + giteeRes);
        return giteeRes;
    }

    /**
     * 使用refresh_token更新access_token
     *
     * @param refresh_token
     * @return
     * @throws UnsupportedEncodingException
     */
    public String refreshToken(String refresh_token) throws UnsupportedEncodingException {
        String qqRes = httpClientComm.post(
                AuthConstant.QQ_OAUTH_TOKEN_URL +
                        "?grant_type=" + REFRESH_TOKEN +
                        "&fmt=json" +
                        "&client_id=" + qqProperty.getCilent_id() +
                        "&redirect_uri=" + qqProperty.getReurl() +
                        "&refresh_token=" + refresh_token);
        return qqRes;

    }

    /**
     * 使用access_token获取当前登录人
     *
     * @param access_token
     * @return
     */
    public String getQQUser(String access_token, String openid) {
        String url = AuthConstant.QQ_OPENAPI_URL + "?access_token=" + access_token +
                "oauth_consumer_key=" + qqProperty.getAdd_id() +
                "openid=" + openid;
        logger.debug("根据获取的access_token调用码云OPENAPI:" + url);
        String giteeRes = httpClientComm.get(url);
        logger.debug("调用获取用户信息的接口，获取到的用户信息是:" + giteeRes);
        return giteeRes;
    }


    /**
     * 创建用户，用户的ID必须是long类型，否则需要转一下
     *
     * @param code
     * @throws Exception
     */
    @ApiMapping(value = "createQQUser", title = "创建qq用户", description = "创建qq用户")
    public String createQQUser(String code) throws Exception {
        logger.debug("------------------------------------------------");
        logger.debug("开始根据用户授权码，获取用户信息");
        String accessToken = getAccessToken(code);
        if (accessToken == null) {
            logger.error("码云认证服务失败,原因是：" + accessToken);
            throw new UserException("码云认证服务失败");
        }
        JSONObject accressTokenJson = JSON.parseObject(accessToken);
        if (accressTokenJson.get("error_description") != null) {
            throw new UserException(String.valueOf(accressTokenJson.get("error_description")));
        }
        String access_token = String.valueOf(accressTokenJson.get("access_token"));
        String open_id = String.valueOf(accressTokenJson.get("open_id"));
        logger.debug("获取的access_token是：" + access_token);
        String qqUserStr = getQQUser(access_token, open_id);
        if (accessToken == null) {
            throw new UserException("qq认证服务失败");
        }
        JSONObject qqUserJson = JSON.parseObject(qqUserStr);
        if (qqUserJson.get("message") != null) {
            logger.error("获取用户信息不对劲：" + qqUserJson.toJSONString());
            throw new UserException(String.valueOf(qqUserJson.get("message")));
        }
        String giteeUserId = String.valueOf(qqUserJson.get("id"));
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUser_extra_id, giteeUserId)
                .eq(UserPO::getLogin_type, LOGIN_QQ.getType()));
        if (userPO == null) {
            logger.debug("获取用户信息成功，开始创建用户");
            UserPO user = UserPO.builder()
                    .id(String.valueOf(snowflakeIdWorker.getSequenceId()))
                    .user_extra_id(String.valueOf(qqUserJson.get("id")))
                    .username(String.valueOf(qqUserJson.get("name")))
                    .email(String.valueOf(qqUserJson.get("email")))
                    .login_type(LOGIN_QQ.getType())
                    .user_right(0)
                    .user_state(1)
                    .user_image(String.valueOf(qqUserJson.get("avatar_url")))
                    .extra_message(qqUserJson.toJSONString())
                    .user_description(String.valueOf(qqUserJson.get("bio")))
                    .build();
            userMapper.insert(user);
            logger.debug("新增qq用户信息成功");
            logger.debug("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        } else {
            logger.debug("获取用户信息成功，开始更新用户");
            UserPO user = UserPO.builder()
                    .username(String.valueOf(qqUserJson.get("name")))
                    .email(String.valueOf(qqUserJson.get("email")))
                    .user_image(String.valueOf(qqUserJson.get("avatar_url")))
                    .extra_message(qqUserJson.toJSONString())
                    .user_description(String.valueOf(qqUserJson.get("bio")))
                    .build();
            userMapper.update(user, new LambdaQueryWrapper<UserPO>()
                    .eq(UserPO::getUser_extra_id, giteeUserId)
                    .eq(UserPO::getLogin_type, LOGIN_QQ.getType()));
            logger.debug("更新qq用户信息成功");
            logger.debug("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        }
    }
}
