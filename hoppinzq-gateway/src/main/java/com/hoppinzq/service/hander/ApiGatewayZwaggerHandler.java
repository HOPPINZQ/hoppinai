package com.hoppinzq.service.hander;

import com.alibaba.fastjson.JSON;
import com.hoppinzq.service.annotation.GatewayServlet;
import com.hoppinzq.service.cache.GatewayCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ZhangQi
 * zwagger处理
 */
@GatewayServlet(prefix = "/service/zwagger", description = "返回zwagger的html")
public class ApiGatewayZwaggerHandler extends AbstractApiGatewayHandler {

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
        StringBuilder s = new StringBuilder();
        s.append("<!DOCTYPE html>");
        s.append("<html lang=\"en\">");
        s.append("<head>");
        s.append("<meta charset=\"UTF-8\">");
        s.append("<title>zwagger</title>");
        s.append("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://hoppinzq.com/static/images/favicon.ico\">");
        s.append("<link rel=\"stylesheet\" href=\"https://hoppinzq.com/main/css/invoice.css\">");
        s.append("</head>").append("<body>");
        s.append("<div class=\"print-button-container\">\n" +
                "    <a href=\"https://gitee.com/hoppin/hoppinzq-gateway\" target=\"_blank\" class=\"print-button\">完整版网关开源地址</a>\n" +
                "    <a href=\"http://hoppin.cn/web/gateway/index.html\" target=\"_blank\" class=\"print-button\">完整版网关文档</a>\n" +
                "</div>");
        s.append(JSON.toJSON(GatewayCache.outApiList));
        return s.toString();
    }
}

