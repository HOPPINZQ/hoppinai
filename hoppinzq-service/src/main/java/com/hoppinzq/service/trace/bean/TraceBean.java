package com.hoppinzq.service.trace.bean;

import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.log.bean.ServiceLogBean;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class TraceBean implements Serializable {

    private String id;
    private String traceId;
    private List<TraceBean> child;
    private String clazz;
    private String method;
    private String type;
    private long costTime;
    private UserPrincipal userPrincipal;
    private long createTime;
    private String exception;
    private List<Map> sqlMap;
    private ServiceLogBean serviceLogBean;

    public TraceBean(String traceId) {
        this.traceId = traceId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public ServiceLogBean getServiceLogBean() {
        return serviceLogBean;
    }

    public void setServiceLogBean(ServiceLogBean serviceLogBean) {
        this.serviceLogBean = serviceLogBean;
        this.method = serviceLogBean.getMethod();
    }

    public List<Map> getSqlMap() {
        return sqlMap;
    }

    public void setSqlMap(List<Map> sqlMap) {
        this.sqlMap = sqlMap;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    public List<TraceBean> getChild() {
        return child;
    }

    public void setChild(List<TraceBean> child) {
        this.child = child;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public UserPrincipal getUserPrincipal() {
        return userPrincipal;
    }

    public void setUserPrincipal(UserPrincipal userPrincipal) {
        this.userPrincipal = userPrincipal;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }


}

