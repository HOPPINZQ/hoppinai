package com.hoppinzq.service.user;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.dal.dao.UserMapper;
import com.hoppinzq.dal.po.UserPO;
import com.hoppinzq.dto.MobileLoginDTO;
import com.hoppinzq.dto.PasswordLoginDTO;
import com.hoppinzq.dto.UserResponseDTO;
import com.hoppinzq.exception.AuthException;
import com.hoppinzq.property.SmsProperty;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.ErrorCode;
import com.hoppinzq.service.constant.UserSignature;
import com.hoppinzq.service.util.ServiceExceptionUtil;
import com.hoppinzq.service.util.SnowflakeIdWorker;
import com.hoppinzq.service.util.UUIDUtil;
import com.hoppinzq.service.util.object.BeanUtils;
import com.hoppinzq.util.EncryptUtil;
import com.hoppinzq.util.Tools;
import com.hoppinzq.utils.RedisUtils;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.sms.v20210111.SmsClient;
import com.tencentcloudapi.sms.v20210111.models.SendSmsRequest;
import com.tencentcloudapi.sms.v20210111.models.SendSmsResponse;
import com.tencentcloudapi.sms.v20210111.models.SendStatus;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

import static com.hoppinzq.enums.LoginTypeEnum.LOGIN_SMS;
import static com.hoppinzq.enums.LoginTypeEnum.LOGIN_WYY;

@Slf4j
@ApiServiceMapping(title = "用户认证服务", description = "用来做用户注册登录登出等功能", roleType = ApiServiceMapping.RoleType.RIGHT)
public class UserService {
    public static final String user2RedisPrefix = "hoppinzq:user:";
    public static final String userPhoneCode2RedisPrefix = user2RedisPrefix + "phone:code:";
    public static final String userPhoneCodeLimit2RedisPrefix = userPhoneCode2RedisPrefix + "limit:";
    public static final String userToken2RedisPrefix = user2RedisPrefix + "token:";
    public static final String userZCode2RedisPrefix = user2RedisPrefix + "zcode:";

    public static final int userRegisterPhoneCodeTimeout = 60 * 10;
    public static final int userCodeEffectiveTime = 10;//5分钟

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private SmsProperty smsProperty;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private HttpServletRequest request;
    @Autowired
    private SnowflakeIdWorker snowflakeIdWorker;

    public static void main(String[] args) throws InterruptedException {
        JwtBuilder jwtBuilder = Jwts.builder();
        String jwtToken = jwtBuilder
                .setHeaderParam("type", "jwt")
                .setHeaderParam("alg", "HS256")
                .claim("user", "123")
                //设置主题 可选
                .setSubject("hoppinzq")
                //有效时间 我设置了7天 从当前时间戳加上7天
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                //设置jwt的ID 我用了UUID
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, UserSignature.signature)
                .compact();

        JwtParser jwtParser = Jwts.parser();
        //传入签名,对jwtToken解密
        Jws<Claims> claimsJws = jwtParser.setSigningKey(UserSignature.signature).parseClaimsJws(jwtToken);
        Claims claims = claimsJws.getBody();
        System.out.println("jwt载荷信息（用户信息）:" + claims.get("user"));
        System.out.println("jwtID:" + claims.getId());
        System.out.println("jwt主题:" + claims.getSubject());
        System.out.println("jwt有效时间：" + claims.getExpiration());

        Thread.sleep(3000);
        Jws<Claims> claimsJws1 = jwtParser.setSigningKey(UserSignature.signature).parseClaimsJws(jwtToken);
        Claims claims1 = claimsJws1.getBody();
        System.out.println("jwt载荷信息（用户信息）:" + claims1.get("user"));
        System.out.println("jwtID:" + claims1.getId());
        System.out.println("jwt主题:" + claims1.getSubject());
        System.out.println("jwt有效时间：" + claims1.getExpiration());
    }

    @ApiMapping(value = "sendMobileCode", title = "发送手机号验证码", description = "发送手机号验证码，返回true表示发送成功", type = ApiMapping.Type.POST)
    public Boolean sendMobileCode(String phone) throws AuthException {
        if (!Tools.checkMobileNumber(phone)) {
            throw new AuthException("手机号格式不正确！");
        }
        if (redisUtils.get(userPhoneCode2RedisPrefix + phone) != null) {
            throw new AuthException("短信验证码仍有效，请过期后尝试！");
        }
        if (redisUtils.get(userPhoneCodeLimit2RedisPrefix + phone) != null) {
            long s = redisUtils.getExpire(userPhoneCodeLimit2RedisPrefix + phone);
            throw new AuthException("您的手机号已被锁定，请" + s + "秒后重试！");
        }
        int codeMobile = Tools.getRandomNum();
        try {
            Credential cred = new Credential(smsProperty.getSecretId(), smsProperty.getSecretKey());
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setReqMethod(smsProperty.getReqMethod());
            httpProfile.setConnTimeout(smsProperty.getConnTimeout());
            httpProfile.setEndpoint(smsProperty.getEndpoint());
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setSignMethod(smsProperty.getSignMethod());
            clientProfile.setHttpProfile(httpProfile);
            SmsClient client = new SmsClient(cred, smsProperty.getRegion(), clientProfile);
            SendSmsRequest req = new SendSmsRequest();
            String sdkAppId = smsProperty.getSdkAppId();
            req.setSmsSdkAppId(sdkAppId);
            String signName = smsProperty.getSignName();
            req.setSignName(signName);
            String senderId = "";
            req.setSenderId(senderId);
            String sessionContext = smsProperty.getSessionContext();
            req.setSessionContext(sessionContext);
            String extendCode = "";
            req.setExtendCode(extendCode);
            String templateId = smsProperty.getLoginTemplateId();
            req.setTemplateId(templateId);
            String[] phoneNumberSet = {"+86" + phone};
            req.setPhoneNumberSet(phoneNumberSet);
            String[] templateParamSet = {String.valueOf(codeMobile), String.valueOf(userCodeEffectiveTime)};//5分钟
            req.setTemplateParamSet(templateParamSet);
            SendSmsResponse res = client.SendSms(req);
            System.out.println(SendSmsResponse.toJsonString(res));
            if (res.getSendStatusSet().length == 1) {
                SendStatus sendStatus = res.getSendStatusSet()[0];
                if ("LimitExceeded.PhoneNumberOneHourLimit".equals(sendStatus.getCode())) {
                    redisUtils.set(userPhoneCodeLimit2RedisPrefix + phone, 1, 60 * 60);
                    throw new AuthException("发送短信失败:该手机号一小时内发送的短信数量超出限制！");
                }
                if ("LimitExceeded.PhoneNumberDailyLimit".equals(sendStatus.getCode())) {
                    redisUtils.set(userPhoneCodeLimit2RedisPrefix + phone, 1, 60 * 60 * 24);
                    throw new AuthException("发送短信失败:该手机号一天内发送的短信数量超出限制！若再次出现此情况，将封禁手机号！");
                }
                if ("".equals(sendStatus.getSerialNo())) {
                    redisUtils.set(userPhoneCode2RedisPrefix + phone, codeMobile, userRegisterPhoneCodeTimeout);
                    throw ServiceExceptionUtil.exception(new ErrorCode(5050, "发送短信失败:短信服务已停用，可以使用: " + codeMobile + " 作为验证码使用！"));
                }
            }
        } catch (TencentCloudSDKException e) {
            throw new AuthException("发送短信失败:" + e);
        }
        redisUtils.set(userPhoneCode2RedisPrefix + phone, codeMobile, userRegisterPhoneCodeTimeout);
        return true;
    }

    @ApiMapping(value = "login", title = "登录", description = "手机号登录，不存在的手机号将会注册", type = ApiMapping.Type.POST)
    public String login(MobileLoginDTO mobileLoginDto) throws AuthException {
        boolean isMobile = Tools.checkMobileNumber(mobileLoginDto.getMobile());
        if (!isMobile) {
            throw new AuthException("手机号格式不正确，请检查手机号");
        }
        String phone = mobileLoginDto.getMobile();
        String code = mobileLoginDto.getCode();
        String rCode = redisUtils.getString(userPhoneCode2RedisPrefix + phone);
        if (rCode == null) {
            throw new AuthException("短信验证码已过期，请重新获取");
        }
        if (!rCode.equals(code)) {
            throw new AuthException("短信验证码不正确，请重新填写");
        }
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>().eq(UserPO::getPhone, phone).eq(UserPO::getLogin_type, "hoppinzq"));
        if (userPO == null) {
            //注册
            UserPO phoneUserPO = UserPO.builder()
                    .id(String.valueOf(snowflakeIdWorker.getSequenceId()))
                    .login_type(LOGIN_SMS.getType())
                    .user_description("这个人很神秘，还没有自我介绍")
                    .phone(phone)
                    .username("匿名用户")
                    .user_image("匿名图片")
                    .user_state(1)
                    .user_right(0)
                    .build();
            userMapper.insert(phoneUserPO);
            UserResponseDTO userResponseDTO = BeanUtils.toBean(phoneUserPO, UserResponseDTO.class);
            return getJWTTokenZCode(JSONObject.toJSONString(userResponseDTO),
                    userToken2RedisPrefix + phoneUserPO.getId());
        } else {
            UserResponseDTO userResponseDTO = BeanUtils.toBean(userPO, UserResponseDTO.class);
            String userMessage = JSONObject.toJSONString(userResponseDTO);
            String jwtToken = getJWTTokenZCode(userMessage, userToken2RedisPrefix + userPO.getId());
            redisUtils.del(userPhoneCode2RedisPrefix + phone);
            return jwtToken;
        }
    }

    @ApiMapping(value = "loginByPsd", title = "登录", description = "用户名密码登录", type = ApiMapping.Type.POST)
    public String loginByPsd(PasswordLoginDTO passwordLoginDto) throws AuthException {
        String username = passwordLoginDto.getUsername();
        String password = passwordLoginDto.getPassword();
        String KEY = password.substring(0, 16);
        String IV = password.substring(16, 32);
        String content = password.substring(32, password.length());
        String decrypted = EncryptUtil.AESDecode(content, KEY, IV);
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>().eq(UserPO::getUsername, username)
                .eq(UserPO::getPassword, decrypted));
        if (userPO == null) {
            throw new AuthException("用户名密码不正确！");
        } else {
            if (userPO.getUser_state() == 0) {
                throw new AuthException("用户已被禁用，登录失败！");
            }
            UserResponseDTO userResponseDTO = BeanUtils.toBean(userPO, UserResponseDTO.class);
            String userMessage = JSONObject.toJSONString(userResponseDTO);
            String jwtToken = getJWTTokenZCode(userMessage, userToken2RedisPrefix + userPO.getId());
            return jwtToken;
        }
    }

    @ApiMapping(value = "logout", title = "登出", description = "登出指定手机号的账户", type = ApiMapping.Type.POST)
    public boolean logout(String phone) {
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>().eq(UserPO::getPhone, phone));
        redisUtils.del(userToken2RedisPrefix + userPO.getId());
        return true;
    }

    @ApiMapping(value = "logoutById", title = "登出通过id", description = "登出通过id", type = ApiMapping.Type.POST)
    public boolean logoutById(Long userId) {
        UserPO userPO = userMapper.selectById(userId);
        if (userPO != null) {
            redisUtils.del(userToken2RedisPrefix + userPO.getId());
        }
//        userPO.setUser_state(0);
//        userPO.setUser_update(new Date());
        userMapper.updateById(userPO);
        return true;
    }

    @ApiMapping(value = "loginByWyy", title = "网易云登录", description = "网易云登录，不存在的将会注册", type = ApiMapping.Type.POST)
    public String loginByWyy(String wyy) throws AuthException {
        JSONObject wyyAccount = JSON.parseObject(wyy);
        Long wyyId = wyyAccount.getLong("userId");
        UserPO userPO = userMapper.selectOne(new LambdaQueryWrapper<UserPO>()
                .eq(UserPO::getUser_extra_id, wyyId)
                .eq(UserPO::getLogin_type, LOGIN_WYY.getType())
        );
        if (userPO != null) {
            log.info("网易云登录");
            // 登录
            UserResponseDTO userResponseDTO = BeanUtils.toBean(userPO, UserResponseDTO.class);
            return getJWTTokenZCode(JSONObject.toJSONString(userResponseDTO),
                    userToken2RedisPrefix + userPO.getId());
        } else {
            // 注册并登录
            log.info("网易云注册");
            UserPO wyyUserPO = UserPO.builder()
                    .id(String.valueOf(snowflakeIdWorker.getSequenceId()))
                    .login_type(LOGIN_WYY.getType())
                    .user_description(wyyAccount.getString("signature"))
                    .username(wyyAccount.getString("nickname"))
                    .extra_message(wyyAccount.toJSONString())
                    .user_extra_id(String.valueOf(wyyId))
                    .user_image(wyyAccount.getString("avatarUrl"))
                    .user_state(1)
                    .user_right(0)
                    .build();
            userMapper.insert(wyyUserPO);
            UserResponseDTO userResponseDTO = BeanUtils.toBean(wyyUserPO, UserResponseDTO.class);
            return getJWTTokenZCode(JSONObject.toJSONString(userResponseDTO),
                    userToken2RedisPrefix + wyyUserPO.getId());
        }
    }

    @ApiMapping(value = "isTokenExpired", title = "token是否有效", description = "token是否有效", type = ApiMapping.Type.POST)
    public boolean isTokenExpired() {
        try {
            String token = request.getHeader("Authorization");
            if (token == null) {
                return false;
            }
            if (token.indexOf("\"") != -1) {
                token = token.substring(1, token.length() - 1);
            }
            JwtParser jwtParser = Jwts.parser();
            jwtParser.setSigningKey(UserSignature.signature)
                    .parseClaimsJws(token);
            return true;
        } catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
    }

    /**
     * 获取jwt的token
     *
     * @param userMessage
     * @param redisKey
     * @return
     */
    public String getJWTToken(String userMessage, String redisKey) {
        JwtBuilder jwtBuilder = Jwts.builder();
        String jwtToken = jwtBuilder
                .setHeaderParam("type", "jwt")
                .setHeaderParam("alg", "HS256")
                .claim("user", userMessage)
                //设置主题 可选
                .setSubject("hoppinzq")
                //有效时间 我设置了7天 从当前时间戳加上7天
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))
                //设置jwt的ID 我用了UUID
                .setId(UUID.randomUUID().toString())
                .signWith(SignatureAlgorithm.HS256, UserSignature.signature)
                .compact();
        redisUtils.set(redisKey, jwtToken, 7 * 24 * 60 * 60);
        return jwtToken;
    }

    @ApiMapping(value = "getJWTTokenByZCode", title = "获取jwt通过zcode", description = "获取jwt通过zcode")
    public String getJWTTokenByZCode(String zcode) {
        String jwtToken = redisUtils.getString(userZCode2RedisPrefix + zcode);
        redisUtils.del(userZCode2RedisPrefix + zcode);
        if (jwtToken == null) {
            return null;
        }
        return jwtToken;
    }

    public String getJWTTokenZCode(String userMessage, String redisKey) {
        String zCode = UUIDUtil.getUUID();
        String jwtToken = getJWTToken(userMessage, redisKey);
        redisUtils.set(userZCode2RedisPrefix + zCode, jwtToken, 60);// zcode有效期
        return zCode;
    }
}

