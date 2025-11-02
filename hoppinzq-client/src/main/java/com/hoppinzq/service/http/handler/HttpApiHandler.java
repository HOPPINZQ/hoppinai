package com.hoppinzq.service.http.handler;

public interface HttpApiHandler {

    void beforeRequest();

    void afterResponse();
}
