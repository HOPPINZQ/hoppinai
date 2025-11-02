package com.hoppinzq.service.hander;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ZhangQi
 * 网关处理接口
 */
public class ApiGatewayNotFoundHandler extends AbstractApiGatewayHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) {
        respondServiceHtml(response);
    }

    public void respondServiceHtml(HttpServletResponse response) {
        try {
            String str = html();
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            OutputStream out = response.getOutputStream();
            out.write(str.getBytes());
            out.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private String html() {
        return "<h1>404 Not Found</h1>";
    }
}

