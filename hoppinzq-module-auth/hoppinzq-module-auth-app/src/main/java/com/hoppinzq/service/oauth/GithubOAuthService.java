package com.hoppinzq.service.oauth;

public interface GithubOAuthService {
    String getAccessToken(String code) throws Exception;

    String getGithubUser(String access_token) throws Exception;

    String createGithubUser(String code) throws Exception;
}
