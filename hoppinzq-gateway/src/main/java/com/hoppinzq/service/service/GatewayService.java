package com.hoppinzq.service.service;

import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.cache.GatewayCache;
import com.hoppinzq.service.config.GatewayServletConfig;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;
import java.util.List;


/**
 * 暴露内部数据
 */
@ApiServiceMapping(title = "网关接口", description = "展示网关相关细节")
public class GatewayService {

    @Autowired
    private HttpServletRequest request;
    @Autowired
    private HttpServletResponse response;

    @ApiMapping(value = "isGateway", title = "服务是否开启网关", description = "返回true表示开启")
    public boolean isGateway() {
        return GatewayServletConfig.isGateway;
    }

    @ApiMapping(value = "getGatewayMapping", title = "获取网关映射规则")
    public List getGatewayMapping() {
        if (!GatewayServletConfig.isGateway) {
            throw new RuntimeException("网关未开启");
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            System.err.println(headerName + ": " + headerValue);
        }
        response.setHeader("zq", "qwe");
        return GatewayCache.servletHandlerMapList;
    }

    @ApiMapping(value = "api", title = "获取API")
    public List getGatewayApi() {
        if (!GatewayServletConfig.isGateway) {
            throw new RuntimeException("网关未开启");
        }
        return GatewayCache.outApiList;
    }

    @ApiMapping(value = "id", title = "获取API", type = ApiMapping.Type.POST)
    public int test(int id) {
        int ii = 8 / id;
        return id;
    }

}
