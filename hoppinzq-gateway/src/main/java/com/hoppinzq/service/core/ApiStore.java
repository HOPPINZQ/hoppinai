package com.hoppinzq.service.core;

import com.hoppinzq.service.annotation.*;
import com.hoppinzq.service.bean.ServiceApiBean;
import com.hoppinzq.service.bean.ServiceMethodAnnotationMapping;
import com.hoppinzq.service.bean.ServiceMethodApiBean;
import com.hoppinzq.service.bean.ServletHandlerMapping;
import com.hoppinzq.service.cache.GatewayCache;
import com.hoppinzq.service.hander.ApiGatewayHandler;
import com.hoppinzq.service.util.AopTargetUtil;
import com.hoppinzq.service.util.StringUtil;
import org.aopalliance.aop.Advice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.Advised;
import org.springframework.context.ApplicationContext;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;


/**
 * api注册中心
 * 该类在启动后会马上装配被注解环绕的类，应该稍微加快点速度
 *
 * @author:ZhangQi
 */
public class ApiStore {
    private static final Logger logger = LoggerFactory.getLogger(ApiStore.class);

    /**
     * API 接口存储map
     */
    private static final Map<String, ApiRunnable> apiMap = GatewayCache.apiMap;
    private static final List<ServiceApiBean> outApiList = GatewayCache.outApiList;
    private static final List<ServletHandlerMapping> registerServletHandler = new ArrayList();
    private static ApplicationContext applicationContext;

    public ApiStore(ApplicationContext applicationContext) {
        ApiStore.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static List<ServletHandlerMapping> getRegisterServletHandler() {
        return registerServletHandler;
    }

    public static void registerMethod(String className, Class<?> type) {
        GatewayServlet gatewayServlet = type.getAnnotation(GatewayServlet.class);
        if (gatewayServlet != null) {
//                Class<?>[] interfaces=type.getInterfaces();
//                    Assert.isTrue(interfaces.length==0, "Servlet必须实现ApiGatewayHandler接口，不符合的类："+type.getName());
//                for(int i=0;i<interfaces.length;i++){
//                    if(interfaces[0].equals(ApiGatewayHandler.class)){
//                        try{
//                            registerServletHandler.add(new ServletHandlerMapping((ApiGatewayHandler)type.getDeclaredConstructor().newInstance(),gatewayServlet.value()));
//                        }catch (Exception ex){
//                            ex.printStackTrace();
//                        }
//                    }
//                }
            try {
                registerServletHandler.add(new ServletHandlerMapping((ApiGatewayHandler) type.getDeclaredConstructor()
                        .newInstance(), gatewayServlet));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        //接口aop解析
        ApiServiceMapping apiServiceMapping = type.getAnnotation(ApiServiceMapping.class);
        if (apiServiceMapping != null) {
            ServiceApiBean serviceApiBean = new ServiceApiBean();
            List<ServiceMethodApiBean> methodList = new ArrayList();
            Boolean isAnnotation = false;
            for (Method m : type.getDeclaredMethods()) {
                ApiMapping apiMapping = m.getAnnotation(ApiMapping.class);
                if (apiMapping != null) {
                    isAnnotation = true;
                    serviceApiBean.setApiServiceTitle(apiServiceMapping.title());
                    serviceApiBean.setApiServiceDescription(apiServiceMapping.description());
                    serviceApiBean.setShow(apiServiceMapping.show());
                    ServiceMethodApiBean serviceMethodApiBean = new ServiceMethodApiBean();

                    //返回值是否封装注解，————
                    ReturnTypeUseDefault returnTypeUseDefault = m.getAnnotation(ReturnTypeUseDefault.class);
                    if (returnTypeUseDefault == null) {
                        serviceMethodApiBean.setMethodReturn(apiMapping.returnType());
                    } else {
                        serviceMethodApiBean.setMethodReturn(false);
                    }

                    //权限
                    ApiMapping.RoleType rightType = apiMapping.roleType();
                    if (apiServiceMapping.roleType() == ApiServiceMapping.RoleType.NO_RIGHT) {
                        rightType = ApiMapping.RoleType.NO_RIGHT;
                    } else if (apiServiceMapping.roleType() == ApiServiceMapping.RoleType.ALL_RIGHT) {
                        rightType = ApiMapping.RoleType.LOGIN;
                    } else if (apiServiceMapping.roleType() == ApiServiceMapping.RoleType.ALL_ADMIN_RIGHT) {
                        rightType = ApiMapping.RoleType.ADMIN;
                    }
                    serviceMethodApiBean.setShow(apiMapping.show());
                    serviceMethodApiBean.setMethodRight(rightType);
                    serviceMethodApiBean.setMethodTitle(apiMapping.title());
                    serviceMethodApiBean.setMethodDescription(apiMapping.description());
                    serviceMethodApiBean.setServiceMethod(apiMapping.value());
                    serviceMethodApiBean.setRequestType(apiMapping.type());
                    //判断服务接口的value是否重复，如果有重复的不让启动
                    Assert.isTrue(checkServiceIsE(serviceMethodApiBean.serviceMethod, methodList, type),
                            StringUtil.isNotEmpty(serviceMethodApiBean.serviceMethod) ?
                                    "在类：" + type.getName() + "里发现重复的服务接口的value值：" + serviceMethodApiBean.serviceMethod : "在类：" + type.getName() + "里发现有接口服务注册的服务名不存在");

                    LocalVariableTableParameterNameDiscoverer u =
                            new LocalVariableTableParameterNameDiscoverer();
                    String[] params = u.getParameterNames(m);
                    List array = new ArrayList();
                    for (int i = 0; i < params.length; i++) {
                        Map object = new HashMap();
                        object.put("serviceMethodParamType", m.getParameterTypes()[i].getCanonicalName());
                        object.put("serviceMethodParamTypeParams", getBeanFileds(m.getParameterTypes()[i]));
                        object.put("serviceMethodParamName", params[i]);
                        array.add(object);
                    }
                    serviceMethodApiBean.setServiceMethodParams(array);
                    Type genericReturnType = m.getGenericReturnType();
                    try {
                        //参数带有泛型的情况，这里我没处理，考虑的情况太多了qaq todo
                        //泛型实际只在编译时起作用，是不是我也可以不去考虑了呢？
                        Type[] actualTypeArguments = ((ParameterizedType) genericReturnType).getActualTypeArguments();
                        for (Type actualTypeArgument : actualTypeArguments) {
                            //System.err.println(actualTypeArgument);
                        }
                    } catch (Exception ex) {
                        //ex.printStackTrace();
                    }

                    serviceMethodApiBean.setServiceMethodReturn(genericReturnType);
                    try {
                        //先只考虑void跟基本数据类型，实体类直接打印实体类名，先不去打印里面字段了
                        if ("void".equals(genericReturnType.getTypeName())) {
                            serviceMethodApiBean.setServiceMethodReturnParams("void");
                        } else {
                            serviceMethodApiBean.setServiceMethodReturnParams(getBeanFileds(Class.forName(genericReturnType.getTypeName())));
                        }
                    } catch (ClassNotFoundException ex) {
                        serviceMethodApiBean.setServiceMethodReturnParams(genericReturnType.getTypeName());
                        //throw new RuntimeException("没有找到类："+genericReturnType.getTypeName());
                    }

                    //解析注解
                    Annotation[] annotations = m.getAnnotations();
                    for (Annotation annotation : annotations) {
                        Class<? extends Annotation> aClass = annotation.annotationType();
                        CustomMapping customMapping = aClass.getAnnotation(CustomMapping.class);
                        ServiceMethodAnnotationMapping serviceMethodAnnotationMappings = new ServiceMethodAnnotationMapping();
                        List<Map> annotationMappings = new ArrayList<>();
                        serviceMethodAnnotationMappings.setAnnotation(aClass);
                        serviceMethodAnnotationMappings.setAnnotationName(aClass.getSimpleName());
                        Method[] annotationMethods = annotation.annotationType().getDeclaredMethods();
                        for (Method annotationMethod : annotationMethods) {
                            try {
                                Map serviceMethodAnnotationMapping = new HashMap();
                                Object value = annotationMethod.invoke(annotation);
                                serviceMethodAnnotationMapping.put(annotationMethod.getName(), value);
                                annotationMappings.add(serviceMethodAnnotationMapping);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        serviceMethodAnnotationMappings.setMapping(annotationMappings);
                        if (customMapping != null) {
                            serviceMethodAnnotationMappings.setCustom(true);
                        }
                        serviceMethodApiBean.setServiceMethodAnnotationMapping(serviceMethodAnnotationMappings);
                    }

                    methodList.add(serviceMethodApiBean);
                    addApiItem(apiMapping, className, m, serviceMethodApiBean);
                }
            }
            if (isAnnotation) {
                serviceApiBean.setServiceMethods(methodList);
                outApiList.add(serviceApiBean);
            }
        }
    }

    /**
     * 检查要注册的接口服务是否已存在
     *
     * @param method
     * @param methodList
     * @return
     */
    private static Boolean checkServiceIsE(String method, List<ServiceMethodApiBean> methodList, Class type) {
        if (!StringUtil.isNotEmpty(method)) {
            logger.error("启动失败！原因是:在类" + type.getName() + "里发现有接口服务注册的服务名不存在");
            return false;
        }
        for (ServiceMethodApiBean serviceMethodApiBean : methodList) {
            if (method.equals(serviceMethodApiBean.getServiceMethod())) {
                logger.error("启动失败！原因是:在类" + type.getName() + "里发现重复的接口服务注册，服务名是:" + method);
                return false;
            }
        }
        return true;
    }

    /**
     * 获取类的参数列表
     *
     * @param beanClass
     * @return
     */
    private static List<Map> getBeanFileds(Class beanClass) {
        List<Map> list = new ArrayList<Map>();
        //判断是否是基本数据类型或String 等
        if (!(isPrimitiveWrapClass(beanClass) ||
                ("java.lang.String".equals(beanClass.getName())))) {
            Field[] fields = beanClass.getDeclaredFields();
            for (Field field : fields) {
                Map map = new HashMap();
                map.put("beanParamName", field.getName());
                map.put("beanParamType", field.getType());
                list.add(map);
            }
        }
        return list;
    }

    /**
     * 判断是否是基本数据类型或者是包装类型
     *
     * @param clz
     * @return
     */
    private static boolean isPrimitiveWrapClass(Class clz) {
        try {
            if (clz.isPrimitive()) {
                return true;
            }
            return ((Class) clz.getField("TYPE").get(null)).isPrimitive();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 查找api
     *
     * @param apiName
     * @return
     */
    public static ApiRunnable findApiRunnable(String apiName) {
        return apiMap.get(apiName);
    }

    /**
     * 添加api
     *
     * @param apiMapping api配置
     * @param beanName   beanq在spring context中的名称
     * @param method
     */
    private static void addApiItem(ApiMapping apiMapping, String beanName, Method method, ServiceMethodApiBean serviceMethodApiBean) {
        ApiRunnable apiRun = new ApiRunnable();
        apiRun.apiName = apiMapping.value();
        apiRun.targetMethod = method;
        apiRun.targetName = beanName;
        apiRun.serviceMethodApiBean = serviceMethodApiBean;
        apiMap.put(apiMapping.value(), apiRun);
    }

    public boolean containsApi(String apiName, String version) {
        return apiMap.containsKey(apiName + "_" + version);
    }

    /**
     * 加载所有bean,扫描api网关注解并存储
     */
    public void loadApiFromSpringBeans() {
        logger.info("开始注册对前端开放接口");
        String[] classNames = applicationContext.getBeanDefinitionNames();
        Class<?> type;
        for (String className : classNames) {
            if ("gatewayServletHandlerRegister".equals(className) || "serverEndpointExporter".equals(className)) {
                continue;
            }
            try {
                Object bean = applicationContext.getBean(className);
                if (bean == this) {
                    continue;
                }
                if (bean instanceof Advised) {
                    try {
                        bean = AopTargetUtil.getTarget(bean);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    type = bean.getClass();
                } else {
                    type = applicationContext.getType(className);
                }
                //servlet处理类和映射解析 一支烟，一壶酒，一个bug改两天
                registerMethod(className, type);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 获取bean的通知对象
     *
     * @param advised
     * @param className
     * @return
     */
    private Advice findAdvice(Advised advised, String className) {
        for (Advisor a : advised.getAdvisors()) {
            if (a.getAdvice().getClass().getName().equals(className)) {
                return a.getAdvice();
            }
        }
        return null;
    }

    public ApiRunnable findApiRunnable(String apiName, String version) {
        return apiMap.get(apiName + "_" + version);
    }

    public List<ApiRunnable> findApiRunnables(String apiName) {
        if (apiName == null) {
            throw new IllegalArgumentException("api name 不能为空!");
        }
        List<ApiRunnable> list = new ArrayList<ApiRunnable>(20);
        for (ApiRunnable api : apiMap.values()) {
            if (api.apiName.equals(apiName)) {
                list.add(api);
            }
        }
        return list;
    }

    public List<ApiRunnable> getAll() {
        List<ApiRunnable> list = new ArrayList<ApiRunnable>(20);
        list.addAll(apiMap.values());
        Collections.sort(list, new Comparator<ApiRunnable>() {
            @Override
            public int compare(ApiRunnable o1, ApiRunnable o2) {
                return o1.getApiName().compareTo(o2.getApiName());
            }
        });
        return list;
    }

}
