package com.hoppinzq.service.oauth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.api.OssService;
import com.hoppinzq.constant.AuthConstant;
import com.hoppinzq.dal.dao.UserMapper;
import com.hoppinzq.dal.po.UserPO;
import com.hoppinzq.property.WeiboProperty;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.exception.UserException;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.user.UserService;
import com.hoppinzq.service.util.SnowflakeIdWorker;
import com.hoppinzq.util.HttpClientComm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;

import static com.hoppinzq.enums.LoginTypeEnum.LOGIN_WEIBO;
import static com.hoppinzq.service.user.UserService.userToken2RedisPrefix;

/**
 * @author: zq
 */
@ServiceRegister
@ApiServiceMapping(title = "weibo第三方认证", description = "weibo第三方认证")
public class WeiboOAuthServiceImpl implements WeiboOAuthService, Serializable {

    private static final long serialVersionUID = 2783377098145240357L;
    private static final String authorization_code = "authorization_code";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final Logger logger = LoggerFactory.getLogger(GiteeOAuthServiceImpl.class);
    @Resource
    private HttpClientComm httpClientComm;
    @Resource
    private WeiboProperty weiboProperty;
    @Resource
    private UserMapper userMapper;
    @Resource
    private ZqServerConfig zqServerConfig;
    @Resource
    private UserService userService;
    @Resource
    private SnowflakeIdWorker snowflakeIdWorker;


    //https://gitee.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code
    //https://gitee.com/oauth/authorize?client_id={client_id}&redirect_uri={redirect_uri}&response_type=code&scope=user_info

    @Override
    public String getAccessToken(String code) throws UnsupportedEncodingException, UserException {
        logger.info("开始获取access_token");
        String postUrl = AuthConstant.WEIBO_OAUTH_TOKEN_URL + "?grant_type=" + authorization_code +
                "&code=" + code +
                "&client_id=" + weiboProperty.getCilent_id() +
                "&client_secret=" + weiboProperty.getClient_secret() +
                "&redirect_uri=" + weiboProperty.getReurl();
        logger.info("请求的微博的url是:" + postUrl);
        String weiboRes = httpClientComm.post(postUrl);
        logger.info("响应是:" + weiboRes);
        //{
        //    "access_token": "2.00A34fQGU7ZhECbd283e44fdqeSEYC",
        //    "remind_in": "157679999",
        //    "expires_in": 157679999, //有效时长
        //    "uid": "5743079592", //用户ID
        //    "isRealName": "true"
        //}
        return weiboRes;
    }

    @Override
    public String getWeiboUser(String access_token, String uid) {
        String url = AuthConstant.WEIBO_OPENAPI_URL + "?access_token=" + access_token + "&uid=" + uid;
        logger.info("根据获取的access_token调用微博OPENAPI:" + url);
        String weiboRes = httpClientComm.get(url);
        logger.info("调用获取用户信息的接口，获取到的用户信息是:" + weiboRes);
        //{
        //    "id": 5743079592,
        //    "idstr": "5743079592",
        //    "class": 1,
        //    "screen_name": "HOPPIN_HAZZ",
        //    "name": "HOPPIN_HAZZ",
        //    "province": "37",
        //    "city": "2",
        //    "location": "山东 青岛",
        //    "description": "防火防盗防狼人",
        //    "url": "",
        //    "profile_image_url": "https://tvax3.sinaimg.cn/crop.0.0.996.996.50/006gFnm0ly8gqsvbl0jehj30ro0ro41e.jpg?KID=imgbed,tva&Expires=1647517334&ssig=WB62U7xiYR",
        //    "cover_image_phone": "http://ww1.sinaimg.cn/crop.0.0.640.640.640/549d0121tw1egm1kjly3jj20hs0hsq4f.jpg",
        //    "profile_url": "u/5743079592",
        //    "domain": "",
        //    "weihao": "",
        //    "gender": "m",
        //    "followers_count": 4,
        //    "followers_count_str": "4",
        //    "friends_count": 60,
        //    "pagefriends_count": 1,
        //    "statuses_count": 6,
        //    "video_status_count": 0,
        //    "video_play_count": 0,
        //    "favourites_count": 0,
        //    "created_at": "Sun Nov 01 10:00:10 +0800 2015",
        //    "following": false,
        //    "allow_all_act_msg": false,
        //    "geo_enabled": true,
        //    "verified": false,
        //    "verified_type": -1,
        //    "remark": "",
        //    "insecurity": {
        //        "sexual_content": false
        //    },
        //    "status": {
        //        "visible": {
        //            "type": 0,
        //            "list_id": 0
        //        },
        //        "created_at": "Sat Feb 19 06:09:45 +0800 2022",
        //        "id": 4738467642475016,
        //        "idstr": "4738467642475016",
        //        "mid": "4738467642475016",
        //        "can_edit": false,
        //        "show_additional_indication": 0,
        //        "text": "http://t.cn/A6ieCu0Z",
        //        "textLength": 20,
        //        "source_allowclick": 0,
        //        "source_type": 1,
        //        "source": "<a href=\"http://app.weibo.com/t/feed/1Nou1F\" rel=\"nofollow\">生日动态</a>",
        //        "favorited": false,
        //        "truncated": false,
        //        "in_reply_to_status_id": "",
        //        "in_reply_to_user_id": "",
        //        "in_reply_to_screen_name": "",
        //        "pic_urls": [],
        //        "geo": null,
        //        "is_paid": false,
        //        "mblog_vip_type": 0,
        //        "annotations": [
        //            {
        //                "mapi_request": true
        //            }
        //        ],
        //        "reposts_count": 0,
        //        "comments_count": 0,
        //        "reprint_cmt_count": 0,
        //        "attitudes_count": 0,
        //        "pending_approval_count": 0,
        //        "isLongText": false,
        //        "reward_exhibition_type": 0,
        //        "hide_flag": 0,
        //        "mlevel": 0,
        //        "biz_ids": [
        //            231601
        //        ],
        //        "biz_feature": 0,
        //        "hasActionTypeCard": 0,
        //        "darwin_tags": [],
        //        "hot_weibo_tags": [],
        //        "text_tag_tips": [],
        //        "mblogtype": 0,
        //        "rid": "0",
        //        "userType": 0,
        //        "more_info_type": 0,
        //        "positive_recom_flag": 0,
        //        "content_auth": 0,
        //        "gif_ids": "",
        //        "is_show_bulletin": 2,
        //        "comment_manage_info": {
        //            "comment_permission_type": -1,
        //            "approval_comment_type": 0,
        //            "comment_sort_type": 0
        //        },
        //        "pic_num": 0,
        //        "reprint_type": 0,
        //        "can_reprint": false,
        //        "new_comment_style": 0
        //    },
        //    "ptype": 0,
        //    "allow_all_comment": true,
        //    "avatar_large": "https://tvax3.sinaimg.cn/crop.0.0.996.996.180/006gFnm0ly8gqsvbl0jehj30ro0ro41e.jpg?KID=imgbed,tva&Expires=1647517334&ssig=m9Mx%2FaRc4C",
        //    "avatar_hd": "https://tvax3.sinaimg.cn/crop.0.0.996.996.1024/006gFnm0ly8gqsvbl0jehj30ro0ro41e.jpg?KID=imgbed,tva&Expires=1647517334&ssig=efK%2FjEsQCi",
        //    "verified_reason": "",
        //    "verified_trade": "",
        //    "verified_reason_url": "",
        //    "verified_source": "",
        //    "verified_source_url": "",
        //    "follow_me": false,
        //    "like": false,
        //    "like_me": false,
        //    "online_status": 0,
        //    "bi_followers_count": 1,
        //    "lang": "zh-cn",
        //    "star": 0,
        //    "mbtype": 0,
        //    "mbrank": 0,
        //    "svip": 0,
        //    "block_word": 0,
        //    "block_app": 0,
        //    "chaohua_ability": 0,
        //    "brand_ability": 0,
        //    "nft_ability": 0,
        //    "credit_score": 80,
        //    "user_ability": 0,
        //    "urank": 4,
        //    "story_read_state": -1,
        //    "vclub_member": 0,
        //    "is_teenager": 0,
        //    "is_guardian": 0,
        //    "is_teenager_list": 0,
        //    "pc_new": 7,
        //    "special_follow": false,
        //    "planet_video": 0,
        //    "video_mark": 0,
        //    "live_status": 0,
        //    "user_ability_extend": 0,
        //    "status_total_counter": {
        //        "total_cnt": 3,
        //        "repost_cnt": 0,
        //        "comment_cnt": 0,
        //        "like_cnt": 3,
        //        "comment_like_cnt": 0
        //    },
        //    "video_total_counter": {
        //        "play_cnt": -1
        //    },
        //    "brand_account": 0,
        //    "hongbaofei": 0
        //}
        return weiboRes;
    }

    @Override
    public String refreshToken(String refresh_token) throws UnsupportedEncodingException {
        String weiboRes = httpClientComm.post(AuthConstant.WEIBO_OAUTH_TOKEN_URL +
                "?grant_type=" + REFRESH_TOKEN +
                "&client_id=" + weiboProperty.getCilent_id() +
                "&client_secret=" + weiboProperty.getClient_secret() +
                "&redirect_uri=" + weiboProperty.getReurl() +
                "&refresh_token=" + refresh_token);
        //{
        //    "access_token": "SlAV32hkKG",
        //    "expires_in": 3600
        //}
        return weiboRes;
    }

    @Override
    @ApiMapping(value = "createWeiboUser", title = "创建Weibo用户", description = "创建Weibo用户")
    public String createWeiboUser(String code) throws Exception {
        logger.info("------------------------------------------------");
        logger.info("开始根据用户授权码，获取用户信息");
        String accressTokenStr = getAccessToken(code);
        if (accressTokenStr == null) {
            logger.error("微博认证服务失败,原因是：" + accressTokenStr);
            throw new UserException("码云认证服务失败");
        }
        JSONObject accressTokenJson = JSON.parseObject(accressTokenStr);
        if (accressTokenJson.get("error") != null) {
            throw new UserException(String.valueOf(accressTokenJson.get("error")));
        }
        String access_token = String.valueOf(accressTokenJson.get("access_token"));
        String uid = String.valueOf(accressTokenJson.get("uid"));
        logger.info("获取的access_token是：" + access_token);
        String weiboUserStr = getWeiboUser(access_token, uid);
        if (accressTokenStr == null) {
            throw new UserException("微博认证服务失败");
        }
        JSONObject weiboUserJson = JSON.parseObject(weiboUserStr);
        if (weiboUserJson.get("error") != null) {
            logger.error("获取用户信息不对劲：" + weiboUserJson.toJSONString());
            throw new UserException(String.valueOf(weiboUserJson.get("error")));
        }
        logger.info("获取用户信息成功，开始创建用户");
        String weiboUserId = String.valueOf(weiboUserJson.get("id"));
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUser_extra_id, weiboUserId)
                .eq(UserPO::getLogin_type, LOGIN_WEIBO.getType()));
        if (userPO == null) {
            logger.info("获取用户信息成功，开始创建用户");
            UserPO user = UserPO.builder()
                    .id(String.valueOf(snowflakeIdWorker.getSequenceId()))
                    .user_extra_id(String.valueOf(weiboUserJson.get("id")))
                    .username(String.valueOf(weiboUserJson.get("name")))
                    .login_type(LOGIN_WEIBO.getType())
                    .user_right(0)
                    .user_state(1)
                    .extra_message(weiboUserStr)
                    .user_description(String.valueOf(weiboUserJson.get("description")))
                    .build();
            String weiboImage = String.valueOf(weiboUserJson.get("profile_image_url"));
            try {
                UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(),
                        zqServerConfig.getPassword());
                OssService ossService = ServiceProxyFactory.createProxy(OssService.class, zqServerConfig.getFileAddr(), upp);
                URL url = new URL(weiboImage);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                String imageUrl = ossService.uploadFile(inputStream);
                user.setUser_image(imageUrl);
            } catch (Exception exception) {
                exception.printStackTrace();
                user.setUser_image(weiboImage);
            }
            userMapper.insert(user);
            logger.info("新增weibo用户信息成功");
            logger.info("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        } else {
            logger.info("获取用户信息成功，开始更新用户");
            UserPO user = UserPO.builder()
                    .username(String.valueOf(weiboUserJson.get("name")))
                    .extra_message(weiboUserStr)
                    .user_description(String.valueOf(weiboUserJson.get("description")))
                    .build();
            String weiboImage = String.valueOf(weiboUserJson.get("profile_image_url"));
            try {
                UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(),
                        zqServerConfig.getPassword());
                OssService ossService = ServiceProxyFactory.createProxy(OssService.class, zqServerConfig.getFileAddr(), upp);
                URL url = new URL(weiboImage);
                URLConnection connection = url.openConnection();
                InputStream inputStream = connection.getInputStream();
                String imageUrl = ossService.uploadFile(inputStream);
                user.setUser_image(imageUrl);
            } catch (Exception exception) {
                exception.printStackTrace();
                user.setUser_image(weiboImage);
            }
            userMapper.update(user, new LambdaQueryWrapper<UserPO>()
                    .eq(UserPO::getUser_extra_id, weiboUserId)
                    .eq(UserPO::getLogin_type, LOGIN_WEIBO.getType()));
            logger.info("更新weibo用户信息成功");
            logger.info("------------------------------------------------");
            user.setExtra_message(null);
            return userService.getJWTTokenZCode(JSONObject.toJSONString(user),
                    userToken2RedisPrefix + user.getId());
        }
    }
}
