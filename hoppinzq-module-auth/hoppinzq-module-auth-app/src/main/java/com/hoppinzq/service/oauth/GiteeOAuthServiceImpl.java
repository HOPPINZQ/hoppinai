package com.hoppinzq.service.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.constant.AuthConstant;
import com.hoppinzq.dal.dao.UserMapper;
import com.hoppinzq.dal.po.UserPO;
import com.hoppinzq.property.GiteeProperty;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.exception.UserException;
import com.hoppinzq.service.user.UserService;
import com.hoppinzq.service.util.SnowflakeIdWorker;
import com.hoppinzq.util.HttpClientComm;
import com.hoppinzq.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import static com.hoppinzq.enums.LoginTypeEnum.LOGIN_GITEE;
import static com.hoppinzq.service.user.UserService.userToken2RedisPrefix;

/**
 * @author: zq
 */
@ServiceRegister
@ApiServiceMapping(title = "gitee第三方认证", description = "gitee第三方认证")
public class GiteeOAuthServiceImpl implements GiteeOAuthService, Serializable {

    private static final long serialVersionUID = 2783377098145240357L;
    private static final String authorization_code = "authorization_code";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final Logger logger = LoggerFactory.getLogger(GiteeOAuthServiceImpl.class);
    @Resource
    private HttpClientComm httpClientComm;
    @Resource
    private GiteeProperty giteeProperty;
    @Resource
    private RedisUtils redisUtils;
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserService userService;
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;


    //https://gitee.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code
    //https://gitee.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code&scope=user_info

    /**
     * 获取access_token跟refresh_token
     *
     * @param code
     * @return
     * @throws UnsupportedEncodingException
     * @throws UserException
     */
    @ApiMapping(value = "giteeToken", title = "获取gitee访问令牌", description = "获取gitee访问令牌")
    public String getAccessToken(String code) throws UnsupportedEncodingException, UserException {
        logger.info("开始获取access_token");
        String postUrl = AuthConstant.GITEE_OAUTH_TOKEN_URL + "?grant_type=" + authorization_code +
                "&code=" + code +
                "&client_id=" + giteeProperty.getCilent_id() +
                "&redirect_uri=" + giteeProperty.getReurl() +
                "&client_secret=" + giteeProperty.getClient_secret();
        logger.info("请求的码云的url是:" + postUrl);
        String giteeRes = httpClientComm.post(postUrl);
        logger.info("响应是:" + giteeRes);
        //{
        //    "access_token": "ee65047cb17f0fdeb8e64ecdc712049c",
        //    "token_type": "bearer",
        //    "expires_in": 86400,
        //    "refresh_token": "09f7942a85bbda4cfc23fdf6bb899ec6cb5c2c740608be63819df19cd15bc3b0",
        //    "scope": "user_info",
        //    "created_at": 1642472291
        //}
        //{
        //    "error": "invalid_grant",
        //    "error_description": "授权方式无效，或者登录回调地址无效、过期或已被撤销"
        //}
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
        String giteeRes = httpClientComm.post(AuthConstant.GITEE_OAUTH_REFRESH_TOKEN_URL +
                "?grant_type=" + REFRESH_TOKEN +
                "&refresh_token=" + refresh_token);
        //{
        //    "access_token": "a142c73141ebb0b64727090e0888c7b9",
        //    "token_type": "bearer",
        //    "expires_in": 86400,
        //    "refresh_token": "12fb24f0f373b25722fd222fd19348a5a9f119be51ccafed2a1aa58e23912c60",
        //    "scope": "user_info",
        //    "created_at": 1642472716
        //}
        return giteeRes;

    }

    /**
     * 使用access_token获取当前登录人
     *
     * @param access_token
     * @return
     */
    public String getGiteeUser(String access_token) {
        String url = AuthConstant.GITEE_OPENAPI_URL + "?access_token=" + access_token + "#/getV5User";
        logger.debug("根据获取的access_token调用码云OPENAPI:" + url);
        String giteeRes = httpClientComm.get(url);
        logger.debug("调用获取用户信息的接口，获取到的用户信息是:" + giteeRes);
        //{
        //    "id": 5294558,
        //    "login": "hoppin",
        //    "name": "hoppin",
        //    "avatar_url": "https://portrait.gitee.com/uploads/avatars/user/1764/5294558_hoppin_1614907809.png",
        //    "url": "https://gitee.com/api/v5/users/hoppin",
        //    "html_url": "https://gitee.com/hoppin",
        //    "remark": "",
        //    "followers_url": "https://gitee.com/api/v5/users/hoppin/followers",
        //    "following_url": "https://gitee.com/api/v5/users/hoppin/following_url{/other_user}",
        //    "gists_url": "https://gitee.com/api/v5/users/hoppin/gists{/gist_id}",
        //    "starred_url": "https://gitee.com/api/v5/users/hoppin/starred{/owner}{/repo}",
        //    "subscriptions_url": "https://gitee.com/api/v5/users/hoppin/subscriptions",
        //    "organizations_url": "https://gitee.com/api/v5/users/hoppin/orgs",
        //    "repos_url": "https://gitee.com/api/v5/users/hoppin/repos",
        //    "events_url": "https://gitee.com/api/v5/users/hoppin/events{/privacy}",
        //    "received_events_url": "https://gitee.com/api/v5/users/hoppin/received_events",
        //    "type": "User",
        //    "blog": "https://blog.csdn.net/qq_41544289",
        //    "weibo": null,
        //    "bio": "Talk is cheap.Show me the code.",
        //    "public_repos": 13,
        //    "public_gists": 0,
        //    "followers": 4,
        //    "following": 4,
        //    "stared": 23,
        //    "watched": 53,
        //    "created_at": "2019-09-09T23:14:04+08:00",
        //    "updated_at": "2022-01-19T09:41:53+08:00",
        //    "email": null
        //}
        return giteeRes;
    }


    /**
     * 创建用户，用户的ID必须是long类型，否则需要转一下
     *
     * @param code
     * @throws Exception
     */
    @ApiMapping(value = "createGiteeUser", title = "创建gitee用户", description = "创建gitee用户")
    public String createGiteeUser(String code) throws Exception {
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
        logger.debug("获取的access_token是：" + access_token);
        String giteeUserStr = getGiteeUser(access_token);
        if (accessToken == null) {
            throw new UserException("码云认证服务失败");
        }
        JSONObject giteeUserJson = JSON.parseObject(giteeUserStr);
        if (giteeUserJson.get("message") != null) {
            logger.error("获取用户信息不对劲：" + giteeUserJson.toJSONString());
            throw new UserException(String.valueOf(giteeUserJson.get("message")));
        }
        String giteeUserId = String.valueOf(giteeUserJson.get("id"));
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUser_extra_id, giteeUserId)
                .eq(UserPO::getLogin_type, LOGIN_GITEE.getType()));
        if (userPO == null) {
            logger.debug("获取用户信息成功，开始创建用户");
            UserPO user = UserPO.builder()
                    .id(String.valueOf(snowflakeIdWorker.getSequenceId()))
                    .user_extra_id(String.valueOf(giteeUserJson.get("id")))
                    .username(String.valueOf(giteeUserJson.get("name")))
                    .email(String.valueOf(giteeUserJson.get("email")))
                    .login_type(LOGIN_GITEE.getType())
                    .user_right(0)
                    .user_state(1)
                    .user_image(String.valueOf(giteeUserJson.get("avatar_url")))
                    .extra_message(giteeUserStr)
                    .user_description(String.valueOf(giteeUserJson.get("bio")))
                    .build();
            userMapper.insert(user);
            logger.debug("新增gitee用户信息成功");
            logger.debug("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        } else {
            logger.debug("获取用户信息成功，开始更新用户");
            UserPO user = UserPO.builder()
                    .username(String.valueOf(giteeUserJson.get("name")))
                    .email(String.valueOf(giteeUserJson.get("email")))
                    .user_image(String.valueOf(giteeUserJson.get("avatar_url")))
                    .extra_message(giteeUserStr)
                    .user_description(String.valueOf(giteeUserJson.get("bio")))
                    .build();
            userMapper.update(user, new LambdaQueryWrapper<UserPO>()
                    .eq(UserPO::getUser_extra_id, giteeUserId)
                    .eq(UserPO::getLogin_type, LOGIN_GITEE.getType()));
            logger.debug("更新gitee用户信息成功");
            logger.debug("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        }
    }
}
