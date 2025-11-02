package com.hoppinzq.service.bean;

import com.hoppinzq.service.util.StringUtil;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author:ZhangQi 服务方法实体类
 */
public class ServiceMethodBean implements Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    private String methodName;
    private String[] methodParamsType;
    private String methodReturnType;
    private String methodTitle;
    private String methodDescription;

    public String getMethodTitle() {
        return methodTitle;
    }

    public void setMethodTitle(String methodTitle) {
        this.methodTitle = methodTitle;
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String[] getMethodParamsType() {
        return methodParamsType;
    }

    public void setMethodParamsType(String[] methodParamsType) {
        this.methodParamsType = methodParamsType;
    }

    public String getMethodReturnType() {
        return methodReturnType;
    }

    public void setMethodReturnType(String methodReturnType) {
        this.methodReturnType = methodReturnType;
    }

    public Map toJSON() {
        Map serviceMethodBeanMap = new HashMap();
        serviceMethodBeanMap.put("methodName", StringUtil.notNull(this.methodName));
        serviceMethodBeanMap.put("methodParamsType", StringUtil.getStaticList(this.methodParamsType));
        serviceMethodBeanMap.put("methodReturnType", StringUtil.notNull(this.methodReturnType));
        serviceMethodBeanMap.put("methodTitle", StringUtil.notNull(this.methodTitle));
        serviceMethodBeanMap.put("methodDescription", StringUtil.notNull(this.methodDescription));
        return serviceMethodBeanMap;
    }
}
