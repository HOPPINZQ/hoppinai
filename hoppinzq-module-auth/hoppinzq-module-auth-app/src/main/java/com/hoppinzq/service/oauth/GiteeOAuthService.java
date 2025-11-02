package com.hoppinzq.service.oauth;

import com.hoppinzq.service.exception.UserException;

import java.io.UnsupportedEncodingException;

public interface GiteeOAuthService {
    String getAccessToken(String code) throws UnsupportedEncodingException, UserException;

    String getGiteeUser(String access_token);

    String refreshToken(String refresh_token) throws UnsupportedEncodingException;

    String createGiteeUser(String code) throws Exception;
}
