package com.hoppinzq.service.oauth;

import com.hoppinzq.service.exception.UserException;

import java.io.UnsupportedEncodingException;

public interface QQOAuthService {
    String getAccessToken(String code) throws UnsupportedEncodingException, UserException;

    String getQQUser(String access_token, String openId);

    String refreshToken(String refresh_token) throws UnsupportedEncodingException;

    String createQQUser(String code) throws Exception;
}
