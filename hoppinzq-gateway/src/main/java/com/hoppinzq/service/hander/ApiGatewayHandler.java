package com.hoppinzq.service.hander;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author ZhangQi
 * 网关处理接口
 */
public interface ApiGatewayHandler {

    void handle(HttpServletRequest request, HttpServletResponse response) throws IOException;
}
