package com.hoppinzq.service.core;

import com.hoppinzq.service.bean.ApiPropertyBean;
import com.hoppinzq.service.bean.ServletHandlerMapping;
import com.hoppinzq.service.cache.GatewayCache;
import com.hoppinzq.service.hander.ApiGatewayHandler;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author:ZhangQi 本地网关servlet
 */
@MultipartConfig
public class APIGatewayServlet<T extends ApiGatewayHandler> extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private ApplicationContext context;
    private ApiPropertyBean apiPropertyBean;

    private T apiHandler = null;
    private String prefix;

    public APIGatewayServlet(T apiHandler, String prefix) {
        this.apiHandler = apiHandler;
        this.prefix = prefix;
    }

    public void setApiPropertyBean(ApiPropertyBean apiPropertyBean) {
        this.apiPropertyBean = apiPropertyBean;
    }

    public void getApiHandler() {
        for (ServletHandlerMapping servletHandlerMapping : GatewayCache.servletHandlerMapList) {
            if (servletHandlerMapping.getPrefix().equals(prefix)) {
                apiHandler = (T) servletHandlerMapping.getServletHandler();
            }
        }
    }

    public String getApiHandlerClassName() {
        return apiHandler.getClass().getSimpleName();
    }

    @Override
    public void init() throws ServletException {
        super.init();
        context = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
        //apiHandler=context.getBean(ApiGatewayHandler.class);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        prefix = request.getServletPath();
        getApiHandler();
        apiHandler.handle(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        prefix = request.getServletPath();
        getApiHandler();
        apiHandler.handle(request, response);
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

    @Override
    protected void doTrace(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}
