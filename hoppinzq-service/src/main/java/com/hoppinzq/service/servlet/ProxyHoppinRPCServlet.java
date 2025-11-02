package com.hoppinzq.service.servlet;

import com.hoppinzq.service.auth.AuthenticationContext;
import com.hoppinzq.service.bean.*;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.common.HoppinInputStreamArgument;
import com.hoppinzq.service.common.HoppinInvocationRequest;
import com.hoppinzq.service.common.HoppinInvocationResponse;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.config.AbstractAbstractRetryRegisterService;
import com.hoppinzq.service.config.RegisterCoreConfig;
import com.hoppinzq.service.exception.RemotingException;
import com.hoppinzq.service.interceptor.service.RPCInterceptor;
import com.hoppinzq.service.log.bean.LogContext;
import com.hoppinzq.service.log.bean.ServiceLogBean;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.serializer.HessionSerializer;
import com.hoppinzq.service.service.RegisterService;
import com.hoppinzq.service.task.TaskStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 注册内部服务
 * 只要服务类被@ServiceRegister注解所环绕，就会被作为内部服务注册
 */
public class ProxyHoppinRPCServlet extends ProxyHoppinServlet {

    private static Logger logger = LoggerFactory.getLogger(ProxyHoppinRPCServlet.class);

    @Override
    public void createServiceWrapper() {
        try {
            List<ServiceWrapper> serviceWrappers = ServiceStore.serviceWrapperList;
            super.setServiceWrappers(serviceWrappers);
            super.createServiceWrapper();
            if (!RegisterCoreConfig.isCore && !"null".equals(this.getPropertyBean().getServerCenter())) {
                registerServiceIntoCore();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public void registerServiceIntoCore() {
        ZqServerConfig zqServerConfig = this.getPropertyBean();
        //一直注册，每分钟尝试一次
        TaskStore.taskQueue.push(new AbstractAbstractRetryRegisterService(zqServerConfig.getRetryCount(), zqServerConfig.getRetryTime(), zqServerConfig.getAlwaysRetry()) {
            @Override
            protected Object toDo() throws RemotingException {
                UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(), zqServerConfig.getPassword());
                List<ServiceWrapper> serviceWrappers = modWrapper();
                List<ServiceWrapperRPC> serviceWrapperRPCS = new ArrayList<>();
                RegisterService registerService = ServiceProxyFactory.createProxy(RegisterService.class, zqServerConfig.getServerCenter(), upp);
                System.err.println("****************************");
                System.err.println(registerService.sayHello(zqServerConfig.getUserName()) + ",连接注册中心成功!");
                for (ServiceWrapper serviceWrapper : serviceWrappers) {
                    ServiceWrapperRPC serviceWrapperRPC = serviceWrapper.toRPCBean();
                    serviceWrapperRPCS.add(serviceWrapperRPC);
                }
                registerService.insertServices(serviceWrapperRPCS);
                System.err.println("****************************");
                logger.info("向注册中心注册服务成功！");
                return true;
            }
        });
    }

    /**
     * 服务方法执行调用请求的实际反序列化并返回
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    public void service(ServletRequest request, ServletResponse response) throws ServletException, IOException {
        AuthenticationContext.enter();
        ServiceLogBean log = (ServiceLogBean) LogContext.enter(new ServiceLogBean());
        RPCBean rpcBean = new RPCBean();
        RPCContext.enter(rpcBean);
        HoppinInvocationResponse hoppinInvocationResponse = new HoppinInvocationResponse();
        HoppinInvocationRequest hoppinInvocationRequest = null;
        Method method = null;
        Object result = null;
        ServiceWrapper serviceWrapper = null;
        try {
            rpcBean.setHoppinInvocationResponse(hoppinInvocationResponse);
            ServletInputStream servletInputStream = request.getInputStream();
            hoppinInvocationRequest = new HessionSerializer().deserialize(servletInputStream, HoppinInvocationRequest.class);
            log.setHeaders(hoppinInvocationRequest.getHeaders());
            rpcBean.setHoppinInvocationRequest(hoppinInvocationRequest);
            serviceWrapper = this.checkWrapper(serviceWrappers, hoppinInvocationRequest.getServiceName());
            if (serviceWrapper == null) {
                throw new RuntimeException("该服务不存在！");
            }
            if (serviceWrapper.getServiceRegisterBean() != null && !serviceWrapper.getServiceRegisterBean().isAvailable()) {
                throw new RuntimeException("该服务已停用！");
            }
            RPCInterceptor.beforeAuthentication(hoppinInvocationRequest, serviceWrapper);
            serviceWrapper.getAuthenticationProvider().authenticate(hoppinInvocationRequest);
            serviceWrapper.getAuthorizationProvider().authorize(hoppinInvocationRequest);
            log.setUserPrincipal((UserPrincipal) AuthenticationContext.getPrincipal());

            Object[] proxiedParameters = serviceWrapper.getModificationManager().applyModificationScheme(hoppinInvocationRequest.getParameters());
            method = getMethod(serviceWrapper, hoppinInvocationRequest.getMethodName(), hoppinInvocationRequest.getParameterTypes());
            rpcBean.setMethod(method.getName());
            rpcBean.setArgs(proxiedParameters);
            log.setMethod(method.getName());
            log.setParams(proxiedParameters);

            log.setName(serviceWrapper.getName());
            log.setType(serviceWrapper.getServiceRegisterBean().getServiceName());

            if (hoppinInvocationRequest.getParameters() != null) {
                for (int i = 0; i < hoppinInvocationRequest.getParameters().length; i++) {
                    if (hoppinInvocationRequest.getParameters()[i] != null && HoppinInputStreamArgument.class.equals(hoppinInvocationRequest.getParameters()[i].getClass())) {
                        proxiedParameters[i] = request.getInputStream();
                        break;
                    }
                }
            }
            RPCInterceptor.beforeMethod(hoppinInvocationRequest, serviceWrapper, rpcBean);
            preMethodInvocation();
            result = method.invoke(serviceWrapper.getService(), proxiedParameters);
            hoppinInvocationResponse.setStatus(200);
            hoppinInvocationResponse.setResult((Serializable) result);
            hoppinInvocationResponse.setModifications(serviceWrapper.getModificationManager().getModifications());
            RPCInterceptor.after(hoppinInvocationResponse, serviceWrapper, rpcBean);
        } catch (Exception e) {
            hoppinInvocationResponse.setStatus(500);
            if (e instanceof InvocationTargetException) {
                InvocationTargetException ite = (InvocationTargetException) e;
                hoppinInvocationResponse.setException(ite.getTargetException());
            } else {
                if (method != null && method.getExceptionTypes() != null) {
                    for (Class exType : method.getExceptionTypes()) {
                        if (exType.isAssignableFrom(e.getClass())) {
                            hoppinInvocationResponse.setException(new RemotingException(e));
                        }
                    }
                }
                if (e instanceof RuntimeException) {
                    hoppinInvocationResponse.setException(new RemotingException(e));
                } else {
                    hoppinInvocationResponse.setException(e);
                }
            }
            RPCInterceptor.exception(hoppinInvocationResponse, serviceWrapper, rpcBean, e);
        } finally {
            AuthenticationContext.exit();
            LogContext.exit();
            postMethodInvocation();
//            Class<? extends Serializer> serializer=rpcBean.getSerializable();
//            if(serializer==null){
//                rpcBean.setSerializable(HessionSerializer.class);
//            }
            if (result != null && result instanceof InputStream) {
                streamResultToResponse(result, response);
            } else {
                try {
                    RPCInterceptor.end(hoppinInvocationRequest, hoppinInvocationResponse, serviceWrapper, rpcBean, log);
                    if (hoppinInvocationRequest != null) {
                        new HessionSerializer().serialize(hoppinInvocationResponse, response.getOutputStream());
                    } else {
                        String resStr = respondServiceHtml();
                        respondWithInterfaceDeclaration(resStr, response);
                    }
                } catch (Exception e) {
                    HoppinInvocationResponse reporter = new HoppinInvocationResponse();
                    reporter.setStatus(500);
                    reporter.setException(new RuntimeException(e.getClass() + "写入结果时出错: " + e.getMessage()));
                    new HessionSerializer().serialize(reporter, response.getOutputStream());
                } finally {
                    RPCContext.exit();
                }
            }
            RPCContext.exit();
        }
    }

    /**
     * 在包装好的服务类上调用注册服务的内部方法
     *
     * @param methodName
     * @param parameterTypes
     * @return
     * @throws NoSuchMethodException
     */
    private Method getMethod(ServiceWrapper serviceWrapper, String methodName, Class[] parameterTypes) throws NoSuchMethodException {
        Class serviceClass = serviceWrapper.getService().getClass();

        while (serviceClass != null) {
            try {
                return serviceClass.getMethod(methodName, parameterTypes);
            } catch (NoSuchMethodException e) {
                serviceClass = serviceClass.getSuperclass();
            }
        }

        throw new NoSuchMethodException(methodName);
    }

    /**
     * 根据服务名找到服务
     *
     * @param serviceWrappers
     * @param serviceName
     * @return
     */
    private ServiceWrapper checkWrapper(List<ServiceWrapper> serviceWrappers, String serviceName) {
        if (serviceName == null) {
            return null;
        }
        for (ServiceWrapper serviceWrapper : serviceWrappers) {
            if (serviceWrapper.isInnerService()) {
                Object bean = getWrapperServicePreBean(serviceWrapper);
                Class<?>[] cs = bean.getClass().getInterfaces();
                for (Class c : cs) {
                    String className = c.getSimpleName();
                    if (className.equals(serviceName)) {
                        return serviceWrapper;
                    }
                }
            } else {
                ServiceRegisterBean serviceRegisterBean = serviceWrapper.getServiceRegisterBean();
                if (serviceName.equals(serviceRegisterBean.getServiceName()) || checkServiceNameAndInterfaceName(serviceName, serviceRegisterBean.getServiceInterfaceName())) {
                    return serviceWrapper;
                }
            }
        }
        return null;
    }

    private Boolean checkServiceNameAndInterfaceName(String serviceName, List<String> interfaceNames) {
        for (String interfaceName : interfaceNames) {
            if (serviceName.equals(interfaceName)) {
                return true;
            }
        }
        return false;
    }

    private void streamResultToResponse(Object result, ServletResponse response) throws IOException {
        InputStream in = (InputStream) result;
        OutputStream out = response.getOutputStream();
        byte[] buf = new byte[getStreamBufferSize()];
        int len;
        while ((len = in.read(buf)) != -1) {
            out.write(buf, 0, len);
        }
    }

    /**
     * 返回描述服务接口的HTML
     *
     * @param response
     */
    private void respondWithInterfaceDeclaration(String str, ServletResponse response) throws IOException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=utf-8");
        OutputStream out = response.getOutputStream();
        out.write(str.getBytes());
        out.close();
    }
}
