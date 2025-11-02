package com.hoppinzq.controller;

import com.hoppinzq.service.oauth.GiteeOAuthService;
import com.hoppinzq.service.oauth.GithubOAuthService;
import com.hoppinzq.service.oauth.QQOAuthService;
import com.hoppinzq.service.oauth.WeiboOAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/oauth2")
@Slf4j
public class OauthController {

    private final static String loginWeb = "https://hoppinzq.com/api/zq-login.html";

    @Autowired
    private HttpServletRequest request;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private GiteeOAuthService giteeOAuthService;
    @Autowired
    private GithubOAuthService githubOAuthService;
    @Autowired
    private QQOAuthService qqOAuthService;
    @Autowired
    private WeiboOAuthService weiboOAuthService;

    /**
     * 获取用户token
     *
     * @param authCode
     * @return
     * @throws Exception
     */
    //接口地址：注意/auth与钉钉登录与分享的回调域名地址一致
    @RequestMapping(value = "/auth", method = RequestMethod.GET)
    public String getAccessToken(@RequestParam(value = "authCode") String authCode) throws Exception {
        System.err.println("authCode" + authCode);
        return authCode;
    }

    @GetMapping(value = "/test")
    public void test(String code, String state, String error, String error_description) throws Exception {
        System.err.println("code:" + code);
        System.err.println("state:" + state);
        System.err.println("error:" + code);
        System.err.println("error_description:" + error_description);
        response.sendRedirect("https://hoppinzq.com?code=" + code + "&state=" + state);
    }

    @RequestMapping("/oauth")
    public void oauth(String code, String type, String state) throws IOException {
        log.info("------------------------------------------------");
        log.info("oauth接口被调用，使用的是：" + type + "鉴权");
        if ("gitee".equals(type)) {
            System.err.println(code);
        }
        log.info("认证完成，设置token完成，即将重定向至：" + (state == null ? loginWeb : state));
        log.info("------------------------------------------------");
        if (state == null) {
            response.sendRedirect(loginWeb + "?code=" + code + "&type=" + type);
        } else {
            response.sendRedirect(state + "?code=" + code + "&type=" + type);
        }
    }

    @RequestMapping("/gitee")
    public void gitee(String code, String type, String state) throws Exception {
        log.info("------------------------------------------------");
        log.info("oauth接口被调用，使用的是：" + type + "鉴权");
        log.info("认证完成，设置token完成，即将重定向至：" + (state == null ? loginWeb : state));
        log.info("------------------------------------------------");
        if (state == null) {
            response.sendRedirect(loginWeb + "?code=" + code + "&type=" + type);
        } else {
            String zcode = giteeOAuthService.createGiteeUser(code);
            response.sendRedirect(state + "?zcode=" + zcode + "&type=" + type);
        }
    }

    @RequestMapping("/weibo")
    public void weibo(String code, String type, String m, String state) throws Exception {
        log.info("------------------------------------------------");
        log.info("oauth接口被调用，使用的是：" + type + "鉴权");
        log.info("认证完成，设置token完成，即将重定向至：" + (state == null ? loginWeb : state));
        log.info("------------------------------------------------");
        if (state == null) {
            response.sendRedirect(loginWeb + "?code=" + code + "&type=" + type);
        } else {
            String zcode = weiboOAuthService.createWeiboUser(code);
            response.sendRedirect(state + "?zcode=" + zcode + "&type=" + type);
        }
    }

    @RequestMapping("/qq")
    public void qq(String code, String type, String m, String state) throws Exception {
        log.info("------------------------------------------------");
        log.info("oauth接口被调用，使用的是：" + type + "鉴权");
        log.info("认证完成，设置token完成，即将重定向至：" + (state == null ? loginWeb : state));
        log.info("------------------------------------------------");
        if (state == null) {
            response.sendRedirect(loginWeb + "?code=" + code + "&type=" + type);
        } else {
            String zcode = qqOAuthService.createQQUser(code);
            response.sendRedirect(state + "?zcode=" + zcode + "&type=" + type);
        }
    }

    @RequestMapping("/github")
    public void github(String code, String type, String m, String state) throws Exception {
        log.info("------------------------------------------------");
        log.info("oauth接口被调用，使用的是：" + type + "鉴权");
        log.info("认证完成，设置token完成，即将重定向至：" + (state == null ? loginWeb : state));
        log.info("------------------------------------------------");
        if (state == null) {
            response.sendRedirect(loginWeb + "?code=" + code + "&type=" + type);
        } else {
            String zcode = githubOAuthService.createGithubUser(code);
            response.sendRedirect(state + "?zcode=" + zcode + "&type=" + type);
        }
    }
}

