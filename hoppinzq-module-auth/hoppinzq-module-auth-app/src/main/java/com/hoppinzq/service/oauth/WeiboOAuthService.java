package com.hoppinzq.service.oauth;

import com.hoppinzq.service.exception.UserException;

import java.io.UnsupportedEncodingException;

public interface WeiboOAuthService {

    String getAccessToken(String code) throws UnsupportedEncodingException, UserException;

    String getWeiboUser(String access_token, String uid);

    String refreshToken(String refresh_token) throws UnsupportedEncodingException;

    String createWeiboUser(String code) throws Exception;
}
