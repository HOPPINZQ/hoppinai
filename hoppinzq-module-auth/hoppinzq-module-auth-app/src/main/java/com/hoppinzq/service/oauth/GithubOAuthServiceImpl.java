package com.hoppinzq.service.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.constant.AuthConstant;
import com.hoppinzq.dal.dao.UserMapper;
import com.hoppinzq.dal.po.UserPO;
import com.hoppinzq.property.GithubProperty;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.exception.UserException;
import com.hoppinzq.service.user.UserService;
import com.hoppinzq.service.util.SnowflakeIdWorker;
import com.hoppinzq.util.HttpClientComm;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import static com.hoppinzq.enums.LoginTypeEnum.LOGIN_GITHUB;
import static com.hoppinzq.service.user.UserService.userToken2RedisPrefix;

/**
 * @author: zq
 */
@ServiceRegister
@ApiServiceMapping(title = "github第三方认证", description = "github第三方认证")
public class GithubOAuthServiceImpl implements GithubOAuthService, Serializable {

    private static final long serialVersionUID = 2783377098145240357L;
    private static final Logger logger = LoggerFactory.getLogger(GithubOAuthServiceImpl.class);
    @Resource
    private HttpClientComm httpClientComm;
    @Resource
    private GithubProperty githubProperty;
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
    @ApiMapping(value = "githubToken", title = "获取github访问令牌", description = "获取github访问令牌")
    public String getAccessToken(String code) throws Exception {
        logger.info("开始获取access_token");
        String postUrl = AuthConstant.GITHUB_OAUTH_TOKEN_URL +
                "&code=" + code +
                "&client_id=" + githubProperty.getCilent_id() +
                "&redirect_uri=" + githubProperty.getReurl() +
                "&client_secret=" + githubProperty.getClient_secret();
        logger.info("请求的github的url是:" + postUrl);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(postUrl)
                .method("POST", body)
                .addHeader("Accept", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        String githubRes = response.body().string();
        logger.info("响应是:" + githubRes);
        return githubRes;
    }

    /**
     * 使用access_token获取当前登录人
     *
     * @param access_token
     * @return
     */
    public String getGithubUser(String access_token) throws Exception {
        String url = AuthConstant.GITHUB_OPENAPI_URL;
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url(url)
                .method("GET", body)
                .addHeader("Authorization", access_token)
                .build();
        Response response = client.newCall(request).execute();
        String githubRes = response.body().string();
        logger.debug("调用获取用户信息的接口，获取到的用户信息是:" + githubRes);
//        {
//            "login": "HOPPINZQ",
//            "id": 58420107,
//            "node_id": "MDQ6VXNlcjU4NDIwMTA3",
//            "avatar_url": "https://avatars.githubusercontent.com/u/58420107?v=4",
//            "gravatar_id": "",
//            "url": "https://api.github.com/users/HOPPINZQ",
//            "html_url": "https://github.com/HOPPINZQ",
//            "followers_url": "https://api.github.com/users/HOPPINZQ/followers",
//            "following_url": "https://api.github.com/users/HOPPINZQ/following{/other_user}",
//            "gists_url": "https://api.github.com/users/HOPPINZQ/gists{/gist_id}",
//            "starred_url": "https://api.github.com/users/HOPPINZQ/starred{/owner}{/repo}",
//            "subscriptions_url": "https://api.github.com/users/HOPPINZQ/subscriptions",
//            "organizations_url": "https://api.github.com/users/HOPPINZQ/orgs",
//            "repos_url": "https://api.github.com/users/HOPPINZQ/repos",
//            "events_url": "https://api.github.com/users/HOPPINZQ/events{/privacy}",
//            "received_events_url": "https://api.github.com/users/HOPPINZQ/received_events",
//            "type": "User",
//            "user_view_type": "public",
//            "site_admin": false,
//            "name": "HOPPIN",
//            "company": "无",
//            "blog": "http://hoppin.cn/",
//            "location": "山东省青岛市",
//            "email": null,
//            "hireable": null,
//            "bio": "我的gitee首页https://gitee.com/hoppin，我的个人网站http://hoppin.cn/\r\n\r\n",
//            "twitter_username": null,
//            "notification_email": null,
//            "public_repos": 8,
//            "public_gists": 0,
//            "followers": 0,
//            "following": 1,
//            "created_at": "2019-12-02T09:00:52Z",
//            "updated_at": "2024-12-19T08:12:09Z"
//        }
        return githubRes;
    }


    /**
     * 创建用户，用户的ID必须是long类型，否则需要转一下
     *
     * @param code
     * @throws Exception
     */
    @ApiMapping(value = "createGithubUser", title = "创建github用户", description = "创建github用户")
    public String createGithubUser(String code) throws Exception {
        logger.debug("------------------------------------------------");
        logger.debug("开始根据用户授权码，获取用户信息");
        String accessToken = getAccessToken(code);
        if (accessToken == null) {
            logger.error("github认证服务失败,原因是：" + accessToken);
            throw new UserException("码云认证服务失败");
        }
        JSONObject accressTokenJson = JSON.parseObject(accessToken);
        if (accressTokenJson.get("error_description") != null) {
            throw new UserException(String.valueOf(accressTokenJson.get("error_description")));
        }
        String access_token = String.valueOf(accressTokenJson.get("access_token"));
        String token_type = String.valueOf(accressTokenJson.get("token_type"));
        logger.debug("获取的access_token是：" + access_token);
        String githubUserStr = getGithubUser(token_type + " " + access_token);
        if (accessToken == null) {
            throw new UserException("github认证服务失败");
        }
        JSONObject githubUserJson = JSON.parseObject(githubUserStr);
        if (githubUserJson.get("message") != null) {
            logger.error("获取用户信息不对劲：" + githubUserJson.toJSONString());
            throw new UserException(String.valueOf(githubUserJson.get("message")));
        }
        String giteeUserId = String.valueOf(githubUserJson.get("id"));
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUser_extra_id, giteeUserId)
                .eq(UserPO::getLogin_type, LOGIN_GITHUB.getType()));
        if (userPO == null) {
            logger.debug("获取用户信息成功，开始创建用户");
            UserPO user = UserPO.builder()
                    .id(String.valueOf(snowflakeIdWorker.getSequenceId()))
                    .user_extra_id(String.valueOf(githubUserJson.get("id")))
                    .username(String.valueOf(githubUserJson.get("login")))
                    .email(String.valueOf(githubUserJson.get("notification_email")))
                    .login_type(LOGIN_GITHUB.getType())
                    .user_right(0)
                    .user_state(1)
                    .user_image(String.valueOf(githubUserJson.get("avatar_url")))
                    .extra_message(githubUserStr)
                    .user_description(String.valueOf(githubUserJson.get("bio")))
                    .build();
            userMapper.insert(user);
            logger.debug("新增github用户信息成功");
            logger.debug("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        } else {
            logger.debug("获取用户信息成功，开始更新用户");
            UserPO user = UserPO.builder()
                    .username(String.valueOf(githubUserJson.get("login")))
                    .email(String.valueOf(githubUserJson.get("notification_email")))
                    .user_image(String.valueOf(githubUserJson.get("avatar_url")))
                    .extra_message(githubUserStr)
                    .user_description(String.valueOf(githubUserJson.get("bio")))
                    .build();
            userMapper.update(user, new LambdaQueryWrapper<UserPO>()
                    .eq(UserPO::getUser_extra_id, giteeUserId)
                    .eq(UserPO::getLogin_type, LOGIN_GITHUB.getType()));
            logger.debug("更新github用户信息成功");
            logger.debug("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        }
    }
}
