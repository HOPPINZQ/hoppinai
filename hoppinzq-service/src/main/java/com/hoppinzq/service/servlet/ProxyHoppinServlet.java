package com.hoppinzq.service.servlet;

import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.service.annotation.InterfaceImplName;
import com.hoppinzq.service.annotation.ServiceOrder;
import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.annotation.ServiceRegisterMethod;
import com.hoppinzq.service.auth.*;
import com.hoppinzq.service.bean.*;
import com.hoppinzq.service.cache.RPCServiceStore;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.config.RegisterCoreConfig;
import com.hoppinzq.service.enums.ServerEnum;
import com.hoppinzq.service.enums.ServiceTypeEnum;
import com.hoppinzq.service.filter.aop.RPCFilter;
import com.hoppinzq.service.interceptor.aop.RPCInterceptor;
import com.hoppinzq.service.log.bean.ServiceLogBean;
import com.hoppinzq.service.log.cache.LogCache;
import com.hoppinzq.service.modification.ModificationManager;
import com.hoppinzq.service.modification.NotModificationManager;
import com.hoppinzq.service.service.HeartbeatService;
import com.hoppinzq.service.service.HeartbeatServiceImpl;
import com.hoppinzq.service.util.AopTargetUtil;
import com.hoppinzq.service.util.UUIDUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author:ZhangQi rpc配置解析servlet
 */
public abstract class ProxyHoppinServlet implements Servlet {

    private static final Integer DEFAULT_STREAM_BUFFER_SIZE = 16384;
    private static Logger logger = LoggerFactory.getLogger(ProxyHoppinServlet.class);
    public final String INIT_PARAM_AUTHENTICATION_PROVIDER = "authenticationProvider";
    public final String INIT_PARAM_AUTHORIZATION_PROVIDER = "authorizationProvider";
    public final String INIT_PARAM_MODIFICATION_MANAGER = "modificationManager";
    public final String INIT_PARAM_SERVICE = "service";
    public final String PARAM_CORE_SERVICE = "RegisterService";
    public final String THIS_REGISTER = "gatewayServletHandlerRegister";
    public List<ServiceWrapper> serviceWrappers;
    public ServletConfig servletConfig;
    private ApplicationContext applicationContext;
    private ZqServerConfig zqServerConfig;

    public List<ServiceWrapper> getServiceWrappers() {
        return serviceWrappers;
    }

    public void setServiceWrappers(List<ServiceWrapper> serviceWrappers) {
        this.serviceWrappers = serviceWrappers;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public ZqServerConfig getPropertyBean() {
        return zqServerConfig;
    }

    public void setPropertyBean(ZqServerConfig zqServerConfig) {
        this.zqServerConfig = zqServerConfig;
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        this.servletConfig = servletConfig;
        String serviceId = UUIDUtil.getUUID();
        zqServerConfig.setId(serviceId);
        ServiceStore.serviceId = serviceId;
        try {
            createServiceWrapper();
            registerHeartbeatServiceNotCheck(new HeartbeatServiceImpl());
        } catch (Exception e) {
            throw new ServletException("未能实例化serviceWrapper", e);
        }
    }

    public void createServiceWrapper() throws InstantiationException, IllegalAccessException {
        String[] classNames = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        for (String className : classNames) {
            if (THIS_REGISTER.equals(className)) {
                continue;
            }
            Object bean = applicationContext.getBean(className);
            Object proxyBean = null;
            if (bean == this) {
                continue;
            }
            if (bean instanceof Advised) {
                try {
                    proxyBean = bean;
                    bean = AopTargetUtil.getTarget(bean);
                } catch (Exception ex) {
                    logger.error("服务注册失败!启动失败！失败类：" + className);
                }
                type = bean.getClass();
            } else {
                type = applicationContext.getType(className);
            }
            ServiceStore.allSpringBeans.add(type);
            //servlet处理类和映射解析
            ServiceRegister serviceRegister = type.getAnnotation(ServiceRegister.class);
            if (serviceRegister != null && serviceRegister.registerType() == ServiceRegister.RegisterType.AUTO) {
                ServiceWrapper serviceWrapper = new ServiceWrapper();
                //读取服务注解配置
                serviceWrapper.setTitle(serviceRegister.title());
                serviceWrapper.setDescription(serviceRegister.description());
                serviceWrapper.setDate(new Date());
                //注册内部服务
                serviceWrapper.setId(this.zqServerConfig.getId());
                serviceWrapper.setName(this.zqServerConfig.getName());
                serviceWrapper.setUsername(this.zqServerConfig.getUserName());
                serviceWrapper.setPassword(this.zqServerConfig.getPassword());
                serviceWrapper.setServiceMessage(serviceWrapper.createInnerServiceMessage(this.zqServerConfig, serviceRegister.title(), serviceRegister.description(), serviceRegister.timeout()));
                if (proxyBean != null) {
                    serviceWrapper.setService(proxyBean);
                } else {
                    serviceWrapper.setService(bean);
                }
                //为内服服务注册用户校验规则，鉴权规则，监听规则及包装细节类（外部服务独有，因为内服服务可以通过反射获取细节）
                SimpleUserCheckAuthenticator singleUsernamePasswordAuthenticator = new SimpleUserCheckAuthenticator();
                singleUsernamePasswordAuthenticator.setUsername(this.zqServerConfig.getUserName());
                singleUsernamePasswordAuthenticator.setPassword(this.zqServerConfig.getPassword());
                serviceWrapper.setAuthenticationProvider(singleUsernamePasswordAuthenticator);

                if (serviceWrapper.getServiceMessage() == null) {
                    serviceWrapper.setServiceMessage(new ServiceMessage());
                }
                serviceWrapper.setAuthenticationProvider(serviceRegister.authentication().newInstance());
                serviceWrapper.setAuthorizationProvider(serviceRegister.authorization().newInstance());
                serviceWrapper.setModificationManager(serviceRegister.modification().newInstance());
                serviceWrapper.setSerializer(serviceRegister.serializer().newInstance());

                if (serviceWrapper.getServiceRegisterBean() == null) {
                    serviceWrapper.setServiceRegisterBean(new ServiceRegisterBean());
                }

                ServiceRegisterBean serviceRegisterBean = new ServiceRegisterBean();
                serviceRegisterBean.setVisible(serviceWrapper.isVisible());
                serviceRegisterBean.setServiceFullName(type.getName());
                serviceRegisterBean.setServiceName(type.getSimpleName());
                List<ServiceMethodBean> serviceMethodBeanList = new ArrayList<>();
                List<String> serviceInterfaceName = new ArrayList<>();
                List<String> serviceInterfaceFullName = new ArrayList<>();

                Class<?>[] interfaces = type.getInterfaces();
                for (Class<?> interfaceClass : interfaces) {
                    serviceInterfaceName.add(interfaceClass.getSimpleName());
                    serviceInterfaceFullName.add(interfaceClass.getName());
                }
                serviceRegisterBean.setServiceInterfaceFullName(serviceInterfaceFullName);
                serviceRegisterBean.setServiceInterfaceName(serviceInterfaceName);
                for (Method m : type.getDeclaredMethods()) {
                    ServiceMethodBean serviceMethodBean = new ServiceMethodBean();
                    //读取注解配置
                    ServiceRegisterMethod registerMethod = m.getAnnotation(ServiceRegisterMethod.class);
                    if (registerMethod != null) {
                        if (!registerMethod.isShow()) {
                            break;
                        }
                        serviceMethodBean.setMethodTitle(registerMethod.title());
                        serviceMethodBean.setMethodDescription(registerMethod.description());
                        //方法注解的规则>类注解上的规则
//                            serviceWrapper.setAuthenticationProvider(serviceRegister.authentication().newInstance());
//                            serviceWrapper.setAuthorizationProvider(serviceRegister.authorization().newInstance());
//                            serviceWrapper.setModificationManager(serviceRegister.modification().newInstance());
//                            serviceWrapper.setSerializer(serviceRegister.serializer().newInstance());
                        serviceMethodBean.setMethodName(m.getName());
                        serviceMethodBean.setMethodReturnType(m.getReturnType().getSimpleName());
                        Class[] cs = m.getParameterTypes();
                        String[] strings = new String[cs.length];
                        for (int i = 0; i < cs.length; i++) {
                            strings[i] = cs[i].getName();
                        }
                        serviceMethodBean.setMethodParamsType(strings);
                        serviceMethodBeanList.add(serviceMethodBean);
                    }

                }
                serviceRegisterBean.setServiceMethodBeanList(serviceMethodBeanList);
                serviceRegisterBean.setServiceClass(type);
                ZqServerConfig zqServerConfig = this.zqServerConfig;
                ServiceMessage serviceMessage = new ServiceMessage(zqServerConfig.getIp(), zqServerConfig.getPort(), zqServerConfig.getPrefix(), ServerEnum.OUTER, serviceWrapper.getServiceMessage());
                serviceWrapper.setServiceMessage(serviceMessage);
                serviceWrapper.setServiceRegisterBean(serviceRegisterBean);
                //标记注册服务RegisterService为REGISTER
                if (type.getName().indexOf(PARAM_CORE_SERVICE) >= 0) {
                    serviceWrapper.setServiceTypeEnum(ServiceTypeEnum.REGISTER);
                }
                serviceWrappers.add(serviceWrapper);
            }
            //拦截器注册
            registerInterceptor(type, className);
            //过滤器注册
            registerFilter(type, className);
            //异常处理器注册
            //registerException(type,className);
        }

    }


    /**
     * 注册拦截器
     *
     * @param type
     * @param className
     */
    private void registerInterceptor(Class<?> type, String className) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes;
        RPCInterceptor rpcInterceptor = type.getAnnotation(RPCInterceptor.class);
        if (rpcInterceptor != null) {
            int index = rpcInterceptor.value();
            ServiceOrder serviceOrder = type.getAnnotation(ServiceOrder.class);
            if (serviceOrder != null) {
                index = serviceOrder.value();
            }
            orderServiceClazzes.add(new OrderServiceClazz(index, type, OrderServiceClazz.OrderServiceTypeEnum.INTERCEPTOR, className));
        }
    }

    /**
     * 注册过滤器
     *
     * @param type
     * @param className
     */
    private void registerFilter(Class<?> type, String className) {
        List<OrderServiceClazz> orderServiceClazzes = ServiceStore.orderServiceClazzes;
        RPCFilter rpcFilter = type.getAnnotation(RPCFilter.class);
        if (rpcFilter != null) {
            int index = rpcFilter.value();
            ServiceOrder serviceOrder = type.getAnnotation(ServiceOrder.class);
            if (serviceOrder != null) {
                index = serviceOrder.value();
            }
            orderServiceClazzes.add(new OrderServiceClazz(index, type, OrderServiceClazz.OrderServiceTypeEnum.FILTER, className));
        }
    }

    /**
     * 注册心跳服务。该服务只是为了让注册中心调用该服务以判断客户端是否还可以正常通讯
     *
     * @param obj
     */
    public final void registerHeartbeatServiceNotCheck(Object obj) {
        if (obj instanceof HeartbeatService) {
            if (obj instanceof Advised) {
                try {
                    obj = AopTargetUtil.getTarget(obj);
                } catch (Exception ex) {
                    logger.error("注册心跳服务失败!启动失败");
                    System.exit(-1);//需要自己定义项目该如何退出，如可能需要保持项目启动失败原因或者一些预热数据
                }
            }
            ServiceWrapper serviceWrapper = new ServiceWrapper();
            serviceWrapper.setId(zqServerConfig.getId());
            serviceWrapper.setName(zqServerConfig.getName());
            serviceWrapper.setDate(new Date());
            serviceWrapper.setTitle("这是心跳服务");
            serviceWrapper.setDescription("注册中心将调用所有注册在注册中心的服务下的心跳服务，用来判断服务状态，心跳服务返回超时或者报错的，将服务状态置为不可用，\n" +
                    "并报告一个错误。心跳服务是集成在jar包里的，凡是要向注册中心注册服务的，必须注册心跳服务（自动注册）。心跳注册不成功，则其他服务不会被注册。");
            serviceWrapper.setService(obj);
            serviceWrapper.setServiceTypeEnum(ServiceTypeEnum.HEARTBEAT);
            serviceWrapper.setAuthenticationProvider(new AuthenticationNotCheckAuthenticator());
            serviceWrapper.setAuthorizationProvider(new AuthenticationNotCheckAuthorizer());
            serviceWrapper.setModificationManager(new NotModificationManager());
            serviceWrapper.setServiceRegisterBean(new ServiceRegisterBean(Boolean.FALSE));
            serviceWrapper.setServiceMessage(new ServiceMessage(zqServerConfig.getIp(), zqServerConfig.getPort(), zqServerConfig.getPrefix(), ServerEnum.INNER));
            serviceWrappers.add(serviceWrapper);
        } else {
            logger.error("注册的不是心跳服务!");
        }
    }

    /**
     * 重写此方法以配置不同的授权提供程序
     * 授权每个经过身份验证的调用
     */
    public AuthorizationProvider getAuthorizationProvider() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (servletConfig.getInitParameter(INIT_PARAM_AUTHORIZATION_PROVIDER) != null) {
            return (AuthorizationProvider) Class.forName(servletConfig.getInitParameter(INIT_PARAM_AUTHORIZATION_PROVIDER)).newInstance();
        }

        return new AuthenticationCheckAuthorizer();
    }

    /**
     * 重写此方法以配置不同的身份验证提供程序
     */
    public AuthenticationProvider getAuthenticationProvider() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (servletConfig.getInitParameter(INIT_PARAM_AUTHENTICATION_PROVIDER) != null) {
            return (AuthenticationProvider) Class.forName(servletConfig.getInitParameter(INIT_PARAM_AUTHENTICATION_PROVIDER)).newInstance();
        }

        return new AuthenticationNotCheckAuthenticator();
    }

    /**
     * 提供要公开的服务
     */
    public Object getService() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return Class.forName(servletConfig.getInitParameter(INIT_PARAM_SERVICE)).newInstance();
    }

    /**
     * 外部服务注册收集参数
     *
     * @return
     */
    public List<ServiceWrapper> modWrapper() {
        List<ServiceWrapper> serviceWrappersCopyList = new ArrayList<>();
        for (ServiceWrapper serviceWrapper : serviceWrappers) {
            ServiceRegisterBean serviceRegisterBean = new ServiceRegisterBean();
            serviceRegisterBean.setVisible(serviceWrapper.isVisible());
            serviceRegisterBean.setServiceClass(serviceWrapper.getService().getClass().getInterfaces()[0]);
            ZqServerConfig zqServerConfig = this.zqServerConfig;
            ServiceMessage serviceMessage = new ServiceMessage(zqServerConfig.getIp(), zqServerConfig.getPort(), zqServerConfig.getPrefix(), ServerEnum.OUTER, serviceWrapper.getServiceMessage());
            ServiceWrapper serviceWrapperCopy = new ServiceWrapper(serviceWrapper.getId(),
                    null, serviceWrapper.getAuthenticationProvider(), serviceWrapper.getAuthorizationProvider(),
                    serviceWrapper.getModificationManager(), serviceWrapper.getSerializer(), serviceMessage, serviceRegisterBean,
                    serviceWrapper.isVisible(), serviceWrapper.isAvailable(), serviceWrapper.getServiceTypeEnum());
            serviceWrappersCopyList.add(serviceWrapperCopy);
        }
        return serviceWrappers;
    }


    /**
     * 如果服务类被代理，获取代理前的服务类
     *
     * @param serviceWrapper
     * @return
     */
    public Object getWrapperServicePreBean(ServiceWrapper serviceWrapper) {
        Object bean = serviceWrapper.getService();
        if (bean instanceof Advised) {
            try {
                return AopTargetUtil.getTarget(bean);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return bean;
    }

    /**
     * 暴露服务，没有可用的调用请求，假设请求不是由客户端发出的。
     *
     * @return html
     */
    public String respondServiceHtml() {
        if (!zqServerConfig.isShowCenter()) {
            return "<h1>该注册中心配置不可见</h1>";
        }
        StringBuilder s = new StringBuilder();
        s.append("<!DOCTYPE html>");
        s.append("<html lang=\"en\">");
        s.append("<head>");
        s.append("<meta charset=\"UTF-8\">");
        s.append("<title>注册中心</title>");
        s.append("<link rel=\"shortcut icon\" type=\"image/x-icon\" href=\"https://hoppinzq.com/static/images/favicon.ico\">");
        s.append("<link rel=\"stylesheet\" href=\"https://hoppinzq.com/zui/static/css/vendors_css.css\">");
        s.append("<link rel=\"stylesheet\" href=\"https://hoppinzq.com/zui/static/css/style.min.css\">");
        s.append("</head>").append("<body>");
        s.append("<section class=\"invoice printableArea\">");
        if (RegisterCoreConfig.isCore) {
            s.append("<div class=\"row\">\n" +
                    "        <div class=\"col-12\">\n" +
                    "            <div class=\"page-header\">\n" +
                    "                <h2 class=\"d-inline\"><span class=\"font-size-30\">注册中心</span></h2>\n" +
                    "                <div class=\"pull-right text-right\">\n" +
                    "                    <h3>注册中心启动时间：" + DateFormatUtils.format(RegisterCoreConfig.startTime, "yyyy-MM-dd hh:mm:ss") + "</h3>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>");
        } else {
            s.append("<div class=\"row\">\n" +
                    "        <div class=\"col-12\">\n" +
                    "            <div class=\"page-header\">\n" +
                    "                <h2 class=\"d-inline\"><span class=\"font-size-30\">本地注册中心</span></h2>\n" +
                    "                <div class=\"pull-right text-right\">\n" +
                    "                    <h5 class=\"text-danger\">注意：这是本地的注册中心，只暴露自己的服务，若要使其成为注册中心，请在启动类上添加<code>@EnableHoppinCore</code>注解</h3>\n" +
                    "                </div>\n" +
                    "            </div>\n" +
                    "        </div>\n" +
                    "    </div>");
        }
        for (ServiceWrapper serviceWrapper : serviceWrappers) {
            if (serviceWrapper.isVisible()) {
                if (!RegisterCoreConfig.isCore && serviceWrapper.getServiceTypeEnum() == ServiceTypeEnum.REGISTER) {
                    continue;
                }
                Object bean = getWrapperServicePreBean(serviceWrapper);
                String serviceMsg = serviceWrapperExtra(serviceWrapper);
                String uri = "http://" + serviceWrapper.getServiceMessage().getServiceIP() + ":" + serviceWrapper.getServiceMessage().getServicePort() + serviceWrapper.getServiceMessage().getServicePrefix();

                s.append("<div class=\"row invoice-info\">\n" +
                        "        <div class=\"col-md-6 invoice-col\">\n" +
                        "            <strong>注册中心内部服务：</strong>\n" +
                        "            <address>\n" +
                        "                <strong class=\"text-blue font-size-24\">服务名:" + serviceWrapper.getName() + "</strong><br>\n" +
                        "                <strong class=\"d-inline\">服务标题:" + serviceWrapper.getTitle() + " </strong><br>\n" +
                        "                <strong>服务描述:" + serviceWrapper.getDescription() + "</strong><br>\n" +
                        "                <strong class=\"text-blue font-size-24\">服务URI:" + uri + "</strong>\n" +
                        "            </address>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-md-6 invoice-col text-right\">\n" +
                        "            <address>\n" +
                        "                <strong class=\"text-blue font-size-24\">服务类名: " + bean.getClass().getSimpleName() + "</strong><br>\n" +
                        "                <strong>服务配置超时时间:" + serviceWrapper.getServiceMessage().getTimeout() + "</strong><br>\n" +
                        "                <strong>服务注册时间:" + serviceWrapper.getDate() + "</strong><br>\n" +
                        "                <strong>服务提供者:" + serviceWrapper.getUsername() + " </strong><br><strong> 服务类型: " + serviceWrapper.getServiceTypeEnum() + "</strong>\n" +
                        "            </address>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-12 invoice-col mb-15\">\n" +
                        "            <div class=\"invoice-details row no-margin\">\n"
                        + serviceMsg +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>");
                s.append("<div class=\"row\">\n" +
                        "        <div class=\"col-12 table-responsive\">");

                s.append("<table class=\"table table-bordered\">\n" +
                        "                <tbody><tr>\n" +
                        "                    <th>返回值</th>\n" +
                        "                    <th>数据类型</th>\n" +
                        "                    <th>方法标题</th>\n" +
                        "                    <th>方法描述</th>\n" +
                        "                </tr>");
                for (ServiceMethodBean method : serviceWrapper.getServiceRegisterBean().getServiceMethodBeanList()) {
                    s.append("<tr><td class=\"returnType\">" + method.getMethodReturnType() + "</td><td class=\"method\">");
                    s.append("<strong>" + method.getMethodName() + "</strong>(");
                    String[] params = method.getMethodParamsType();
                    if (params != null && params.length > 0) {
                        for (int i = 0; i < params.length; i++) {
                            if (i > 0) {
                                s.append(", ");
                            }
                            s.append(params[i] + " arg" + i);
                        }
                    }
                    s.append(")</td>");
                    s.append("<td>" + method.getMethodTitle() + "</td>");
                    s.append("<td>" + method.getMethodDescription() + "</td>");
                    s.append("</tr>");
                }
                s.append("</tbody></table>");
                s.append("</div></div>");
                int logs = 0;
                long time = 0;
                int successNum = 0;
                int failNum = 0;
                for (String log : LogCache.logList) {
                    ServiceLogBean serviceLogBean = JSONObject.parseObject(log, ServiceLogBean.class);
                    if (serviceLogBean.getType().equals(serviceWrapper.getServiceRegisterBean().getServiceName())) {
                        logs++;
                        if (!serviceLogBean.isError()) {
                            successNum++;
                            time += serviceLogBean.getTime();
                        } else {
                            failNum++;
                        }
                    }
                }
                long avaTime = 0;
                if (successNum != 0) {
                    avaTime = time / successNum;
                }

                s.append("<div class=\"row\">\n" +
                        "        <div class=\"col-12 text-right\">" +
                        "<p class=\"lead\"><b>平均调用时间：</b><span class=\"text-danger\"> " + avaTime + " ms</span></p>" +
                        "            <div>\n" +
                        "                <p>成功次数  :  " + successNum + "</p>\n" +
                        "                <p>失败次数  :  " + failNum + "</p>\n" +
                        "            </div>\n" +
                        "            <div class=\"total-payment\">\n" +
                        "                <h3><b>总调用次数 :</b> " + logs + "</h3>\n" +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>");
                s.append("<div class=\"row no-print\">\n" +
                        "        <div class=\"col-12\">\n" +
                        "            <a class=\"btn btn-success pull-right ml-2\" onclick=\"alert('还没做好')\">获取服务接口类</a>" +
                        "            <a class=\"btn btn-success pull-right ml-2\" target=\"_blank\" href=\"https://hoppinzq.com/springhoppinzq/index.html?center=" + uri + "\">查看具体日志</a>" +
                        "            <a class=\"btn btn-success pull-right ml-2\" onclick=\"alert('禁止查看')\">查看配置文件</a>" +
                        "        </div>\n" +
                        "    </div>");
                s.append("<hr style=\"border: 3px solid;margin: 2rem auto;\">");
            }
        }
        for (ServiceWrapperRPC serviceWrapper : RPCServiceStore.serviceWrapperRPCList) {
            if (serviceWrapper.getVisible()) {
                if (serviceWrapper.getServiceTypeEnum() == ServiceTypeEnum.REGISTER) {
                    continue;
                }

                Map registerBean = serviceWrapper.getServiceRegisterBean();
                String serviceMsg = outerServiceWrapperExtra(serviceWrapper);
                String uri = "http://" + serviceWrapper.getServiceMessage().get("serviceIP") + ":" + serviceWrapper.getServiceMessage().get("servicePort") + serviceWrapper.getServiceMessage().get("servicePrefix");
                s.append("<div class=\"row invoice-info\">\n" +
                        "        <div class=\"col-md-6 invoice-col\">\n" +
                        "            <strong class=\"text-blue font-size-24\">外部服务：</strong>\n" +
                        "            <address>\n" +
                        "                <strong class=\"text-blue font-size-24\">服务名:" + serviceWrapper.getName() + "</strong><br>\n" +
                        "                <strong class=\"d-inline\">服务标题:" + serviceWrapper.getTitle() + " </strong><br>\n" +
                        "                <strong>服务描述:" + serviceWrapper.getDescription() + "</strong><br>\n" +
                        "                <strong class=\"text-blue font-size-24\">服务URI:" + uri + "</strong>\n" +
                        "            </address>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-md-6 invoice-col text-right\">\n" +
                        "            <address>\n" +
                        "                <strong class=\"text-blue font-size-24\">服务类名:" + serviceWrapper.getServiceRegisterBean().get("serviceName") + "</strong><br>\n" +
                        "                <strong>服务配置超时时间:" + serviceWrapper.getServiceMessage().get("timeout") + "</strong><br>\n" +
                        "                <strong>服务注册时间:" + serviceWrapper.getDate() + "</strong><br>\n" +
                        "                <strong>服务提供者:" + serviceWrapper.getUsername() + " </strong><br><strong> 服务类型: " + serviceWrapper.getServiceTypeEnum() + "</strong>\n" +
                        "            </address>\n" +
                        "        </div>\n" +
                        "        <div class=\"col-sm-12 invoice-col mb-15\">\n" +
                        "            <div class=\"invoice-details row no-margin\">\n"
                        + serviceMsg +
                        "            </div>\n" +
                        "        </div>\n" +
                        "    </div>");
                s.append("<div class=\"row\">\n" +
                        "        <div class=\"col-12 table-responsive\">");

                s.append("<table class=\"table table-bordered\">\n" +
                        "                <tbody><tr>\n" +
                        "                    <th>返回值</th>\n" +
                        "                    <th>数据类型</th>\n" +
                        "                    <th>方法标题</th>\n" +
                        "                    <th>方法描述</th>\n" +
                        "                </tr>");
                for (Map method : (List<Map>) registerBean.get("serviceMethodBeanList")) {
                    s.append("<tr><td class=\"returnType\">" + method.get("methodReturnType") + "</td><td class=\"method\">");
                    s.append("<strong>" + method.get("methodName") + "</strong>(");
                    List params = (List) method.get("methodParamsType");
                    if (params != null && params.size() > 0) {
                        for (int i = 0; i < params.size(); i++) {
                            if (i > 0) {
                                s.append(", ");
                            }
                            s.append(params.get(i) + " arg" + i);
                        }
                    }
                    s.append(")</td>");
                    s.append("<td>" + method.get("methodTitle") + "</td>");
                    s.append("<td>" + method.get("methodDescription") + "</td>");
                    s.append("</tr>");
                }
                s.append("</tbody></table>");
                s.append("</div></div>");
                s.append("<div class=\"row no-print\">\n" +
                        "        <div class=\"col-12\">\n" +
                        "            <a class=\"btn btn-success pull-right ml-2\">获取服务接口类</a>" +
                        "            <a class=\"btn btn-success pull-right ml-2 target=\"_blank\" href=" + uri + ">转到该服务的注册中心</a>" +
                        "        </div>\n" +
                        "    </div>");
                s.append("<hr style=\"border: 3px solid;margin: 2rem auto;\">");
            }
        }
        s.append("</section>").append("</body>").append("</html>");
        //html美观
        return s.toString();
    }

    private String serviceWrapperExtra(ServiceWrapper serviceWrapper) {
        StringBuilder s = new StringBuilder();
        if (serviceWrapper.isAvailable()) {
            s.append("<div class=\"col-md-6 col-lg-2\"><b>服务状态:</b><span class=\"text-success\">可用</span></div>");
        } else {
            s.append("<div class=\"col-md-6 col-lg-2\"><b>服务状态:</b><span class=\"text-danger\">不可用</span></div>");
        }

        s.append("<div class=\"col-md-6 col-lg-3\"><b>用户认证方式:</b>");
        InterfaceImplName interfaceImplName = serviceWrapper.getAuthenticationProvider().getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName != null) {
            s.append("<span class=\"text-success\">" + interfaceImplName.value() + "</span>");
        } else {
            s.append("<span class=\"text-danger\">未知的用户认证方式</span>");
        }
        s.append("</div>");

        s.append("<div class=\"col-md-6 col-lg-2\"><b>权限认证方式:</b>");
        InterfaceImplName interfaceImplName1 = serviceWrapper.getAuthorizationProvider().getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName1 != null) {
            s.append("<span class=\"text-success\">" + interfaceImplName1.value() + "</span>");
        } else {
            s.append("<span class=\"text-danger\">未知的权限认证方式</span>");
        }
        s.append("</div>");

        s.append("<div class=\"col-md-6 col-lg-2\"><b>序列化方式:</b>");
        InterfaceImplName interfaceImplName2 = serviceWrapper.getSerializer().getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName2 != null) {
            s.append("<span class=\"text-success\">" + interfaceImplName2.value() + "</span>");
        } else {
            s.append("<span class=\"text-danger\">未知的序列化方式</span>");
        }
        s.append("</div>");

        s.append("<div class=\"col-md-6 col-lg-3\"><b>参数跟踪方式:</b>");
        InterfaceImplName interfaceImplName3 = serviceWrapper.getModificationManager().getClass().getAnnotation(InterfaceImplName.class);
        if (interfaceImplName3 != null) {
            s.append("<span class=\"text-success\">" + interfaceImplName3.value() + "</span>");
        } else {
            s.append("<span class=\"text-danger\">未知的参数跟踪方式</span>");
        }
        s.append("</div>");
        return s.toString();
    }

    private String outerServiceWrapperExtra(ServiceWrapperRPC serviceWrapper) {
        StringBuilder s = new StringBuilder();
        if (serviceWrapper.getAvailable()) {
            s.append("<div class=\"col-md-6 col-lg-2\"><b>服务状态:</b><span class=\"text-success\">可用</span></div>");
        } else {
            s.append("<div class=\"col-md-6 col-lg-2\"><b>服务状态:</b><span class=\"text-danger\">不可用</span></div>");
        }
        s.append("<div class=\"col-md-6 col-lg-3\"><b>用户认证方式:</b>");
        s.append(serviceWrapper.getAuthenticationProvider().substring(serviceWrapper.getAuthenticationProvider().lastIndexOf(".") + 1));
        s.append("</div>");
        s.append("<div class=\"col-md-6 col-lg-2\"><b>权限认证方式:</b>");
        s.append(serviceWrapper.getAuthorizationProvider().substring(serviceWrapper.getAuthorizationProvider().lastIndexOf(".") + 1));
        s.append("</div>");
//        s.append("<div class=\"col-md-6 col-lg-2\"><b>序列化方式:</b>");
//        s.append(serviceWrapper.getAuthorizationProvider().substring(serviceWrapper.getSerializer().lastIndexOf(".")+1));
//        s.append("</div>");
        s.append("<div class=\"col-md-6 col-lg-3\"><b>参数跟踪方式:</b>");
        s.append(serviceWrapper.getModificationManager().substring(serviceWrapper.getModificationManager().lastIndexOf(".") + 1));
        s.append("</div>");
        return s.toString();
    }


    /**
     * 重写该方法以在对服务的方法进行返回后执行自定义工作
     */
    public void postMethodInvocation() {

    }

    /**
     * 重写该方法以在服务上调用方法之前执行一些其他事情
     */
    public void preMethodInvocation() {

    }


    public ModificationManager getModificationManager() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        if (servletConfig.getInitParameter(INIT_PARAM_MODIFICATION_MANAGER) != null) {
            return (ModificationManager) Class.forName(servletConfig.getInitParameter(INIT_PARAM_MODIFICATION_MANAGER)).newInstance();
        }

        return new NotModificationManager();
    }


    @Override
    public String getServletInfo() {
        return getClass().getCanonicalName();
    }

    @Override
    public void destroy() {

    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }


    public Integer getStreamBufferSize() {
        return DEFAULT_STREAM_BUFFER_SIZE;
    }
}
