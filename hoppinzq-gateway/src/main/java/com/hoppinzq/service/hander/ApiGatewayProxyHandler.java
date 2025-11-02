package com.hoppinzq.service.hander;

import com.hoppinzq.service.annotation.GatewayServlet;
import com.hoppinzq.service.constant.ApiCommConstant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;

/**
 * @author ZhangQi
 * 代理网关
 */
@GatewayServlet(prefix = "/service/proxy", description = "提供接口代理和https转http请求")
public class ApiGatewayProxyHandler extends AbstractApiGatewayHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if ("GET".equals(request.getMethod())) {
            // 获取代理目标的URL
            String targetUrl = "http://127.0.0.1:8802/service/hoppinzq";
//            if (targetUrl == null || targetUrl.isEmpty()) {
//                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing target URL");
//                return;
//            }
            String method = request.getParameter(ApiCommConstant.METHOD);
            String params = request.getParameter(ApiCommConstant.PARAMS);
            targetUrl += "?method=" + method + "&params=" + params;
            // 创建代理连接
            URL url = new URL(targetUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 设置请求头
            Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                String headerValue = request.getHeader(headerName);
                connection.setRequestProperty(headerName, headerValue);
            }

            // 发送请求并获取响应
            int responseCode = connection.getResponseCode();
            response.setStatus(responseCode);
            // 复制响应头
            for (String headerName : connection.getHeaderFields().keySet()) {
                if (headerName != null) {
                    String headerValue = connection.getHeaderField(headerName);
                    response.setHeader(headerName, headerValue);
                }
            }
            // 复制响应内容
            InputStream inputStream = connection.getInputStream();
            OutputStream outputStream = response.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            //todo 没有处理4xx和5xx
            // 关闭连接
            outputStream.close();
            inputStream.close();
            connection.disconnect();
        } else {
            String str = "POST请求暂未实现";
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html; charset=utf-8");
            OutputStream out = response.getOutputStream();
            out.write(str.getBytes());
            out.close();
        }
    }

}

