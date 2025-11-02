package com.hoppinzq.service.hander;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.api.AuthService;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.GatewayServlet;
import com.hoppinzq.service.bean.*;
import com.hoppinzq.service.cache.GatewayConfigBean;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.constant.UserSignature;
import com.hoppinzq.service.exception.ResultReturnException;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static com.hoppinzq.service.util.ServiceExceptionUtil.exception;


/**
 * 网关参数处理
 * 通过继承本类，自定义网关的默认行为，如重写afterSuccessRequest方法以在网关成功响应前做一些你自己的事情。
 * 通过RequestContext类，获取该线程本次请求所有信息。使用这些信息，你可以在自定义重写方法做一些事情，如获取用户信息进行鉴权等。
 *
 * @author:ZhangQi
 */
@GatewayServlet(prefix = "/service/hoppinzq", description = "这是自带的网关")
public class ApiGatewayDefaultHandler extends AbstractApiGatewayHandler {
    private static final Logger logger = LoggerFactory.getLogger(ApiGatewayDefaultHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        super.handle(request, response);
    }

    @Override
    public Boolean right(HttpServletRequest request, HttpServletResponse response, RequestParam requestParam) throws IOException {
//        if(!apiPropertyBean.isAuth()){
//            return true;
//        }
        corsAttack(request, response);
        LoginUser.enter();
        ServiceMethodApiBean serviceMethodApiBean = requestParam.getApiRunnable().getServiceMethodApiBean();
        String token = request.getHeader("Authorization");
        String user = null;
        if (token != null) {
            try {
                if (token.indexOf("\"") != -1) {
                    token = token.replace("\"", "");
                }
                JwtParser jwtParser = Jwts.parser();
                //传入签名,对jwtToken解密
                Jws<Claims> claimsJws = jwtParser.setSigningKey(UserSignature.signature).parseClaimsJws(token);
                Claims claims = claimsJws.getBody();
                user = claims.get("user").toString();
            } catch (Exception e) {
                user = null;
            }
        }
        if (serviceMethodApiBean.methodRight != ApiMapping.RoleType.NO_RIGHT) {
            if (null == user) {
                redirectUrl(request, response);
                return false;
            } else {
                // token在redis里失效
                JSONObject userJson = JSON.parseObject(user);
                Long user_id = userJson.getLong("id");
                UserPrincipal upp = new UserPrincipal(GatewayConfigBean.zqServerConfig.getUserName(), GatewayConfigBean.zqServerConfig.getPassword());
                AuthService authService = ServiceProxyFactory.createProxy(AuthService.class, GatewayConfigBean.apiPropertyBean.getSsoUrl(), upp);
                Boolean ok = authService.isToken(user_id);
                if (!ok) {
                    redirectUrl(request, response);
                    return false;
                }
            }
        }
        requestParam.setUser(user);
        LoginUser.setUserHold(user);
        return true;
    }

    //防重放攻击
    private void corsAttack(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String ignored = request.getHeader("X-ZQ-Ignore");
        if (ignored != null) {
            return;
        }
        //AntPathMatcher antPathMatcher=new AntPathMatcher();
        String nonce = request.getHeader("X-ZQ-Nonce");
        String timestamp = request.getHeader("X-ZQ-TimeStamp");
        String sign = request.getHeader("X-ZQ-Sign");

        // 验证请求头是否存在
        if (StringUtils.isEmpty(nonce) ||
                StringUtils.isEmpty(timestamp) ||
                StringUtils.isEmpty(sign)) {
            throw exception(new ErrorCode(4001, "请求头缺少必要参数"));
        }
        // 格式校验
        if (sign.length() != 32 ||
                timestamp.length() != 13 ||
                nonce.length() != 32) {
            throw exception(new ErrorCode(4002, "请求头参数格式不正确"));
        }

        long now = System.currentTimeMillis();
        if (now - Long.parseLong(timestamp) > 60 * 1000) {
            throw exception(new ErrorCode(4003, "请求过期"));
        }

        boolean nonceExists = false;
//        try {
//            nonceExists= Boolean.TRUE.equals(redisTemplate.hasKey("x-nonce-" + nonce));
//        } catch (Exception e) {
//            e.printStackTrace();
//            nonceExists = false;
//        }
//        if (nonceExists) {
//            throw exception(new ErrorCode(4004, "请求重复"));
//        } else {
//            redisTemplate.opsForValue().set("x-nonce-" + nonce, nonce, 60, TimeUnit.SECONDS);
//        }
        boolean accept = sign.equals(DigestUtils.md5DigestAsHex((nonce + timestamp).getBytes()));
        if (!accept) {
            throw exception(new ErrorCode(4005, "签名校验失败"));
        }
    }

    /**
     * 重定向url
     *
     * @param request
     * @param response
     * @throws IOException
     */
    private void redirectUrl(HttpServletRequest request, HttpServletResponse response) throws IOException {
        RequestParam requestParam = (RequestParam) RequestContext.getPrincipal();
        ServiceMethodApiBean serviceMethodApiBean = requestParam.getApiRunnable().getServiceMethodApiBean();
        //Ajax请求
        if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
            throw new ResultReturnException(ErrorEnum.COMMON_USER_TOKEN_OUT_DATE);
//            String sourceUrl=request.getHeader("Referer");
//            if(null==sourceUrl){
//                sourceUrl=request.getRequestURL().toString();
//            }
////            if(serviceMethodApiBean.methodRight== ApiMapping.RoleType.ADMIN){
////                response.setHeader("redirect", apiPropertyBean.getSsoAdminUrl() + "?redirect=" +sourceUrl);
////            }else{
////                response.setHeader("redirect", apiPropertyBean.getSsoUrl() + "?redirect=" +sourceUrl);
////            }
//            if(serviceMethodApiBean.methodRight== ApiMapping.RoleType.ADMIN){
//                response.setHeader("redirect", "https://hoppinzq.com" + "?redirect=" +sourceUrl);
//            }else{
//                response.setHeader("redirect", "https://hoppinzq.com/api/zq-login.html" + "?redirect=" +sourceUrl);
//            }
//            clearThreadLocal();
//            response.setHeader("enableRedirect","true");
//            response.addHeader("Access-Control-Expose-Headers","redirect,enableRedirect");
//            response.setStatus(302);
//            response.flushBuffer();
        }
        //浏览器地址栏请求
        else {
            String queryString = request.getQueryString();
            String requestURL = String.valueOf(request.getRequestURL());
            String realUrl = requestURL + "?" + queryString;
            //跳转到登录页面，把用户请求的url作为参数传递给登录页面。
//            if(serviceMethodApiBean.methodRight== ApiMapping.RoleType.ADMIN){
//                response.sendRedirect(apiPropertyBean.getSsoAdminUrl() + "?redirect=" + realUrl);
//            }else{
//                response.sendRedirect(apiPropertyBean.getSsoUrl() + "?redirect=" + realUrl);
//            }
            if (serviceMethodApiBean.methodRight == ApiMapping.RoleType.ADMIN) {
                response.setHeader("redirect", "https://hoppinzq.com" + "?redirect=" + realUrl);
            } else {
                response.setHeader("redirect", "https://hoppinzq.com/api/zq-login.html" + "?redirect=" + realUrl);
            }
            clearThreadLocal();
        }
    }

    /**
     * 网关统一返回值封装返回值（重写）
     *
     * @param request
     * @param response
     * @param result
     * @throws IOException
     */
    @Override
    public void setOutParam(HttpServletRequest request, HttpServletResponse response, Object result, RequestParam requestParam) throws IOException {
        response.setContentType("text/html; charset=UTF-8");
        PrintWriter out = response.getWriter();
        if (requestParam.getApiRunnable() == null) {
            out.println(result.toString());
        } else {
            ServiceMethodApiBean serviceMethodApiBean = requestParam.getApiRunnable().getServiceMethodApiBean();
            if (serviceMethodApiBean.methodReturn) {
                out.println(JSON.toJSONString(result));
            } else {
                JSONObject resultJson = JSONObject.parseObject(result.toString());
                //配置不封装返回值的情况：若报错，依然使用封装的返回值
                if (resultJson.get("data") == null) {
                    out.println(resultJson);
                } else {
                    out.println(resultJson.get("data").toString());
                }

            }
        }
    }

    private void clearThreadLocal() {

    }

}
