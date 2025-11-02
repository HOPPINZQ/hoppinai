package com.hoppinzq.service.bean;

import com.hoppinzq.service.exception.ServiceException;
import com.hoppinzq.service.util.DateFormatUtil;
import com.hoppinzq.service.util.IPUtils;
import com.hoppinzq.service.util.UUIDUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author:ZhangQi 请求封装信息类，请求日志类
 **/
@Data
@NoArgsConstructor
public class RequestInfo implements Serializable {
    private static final long serialVersionUID = 2783377098145240357L;

    private String id;//日志ID
    private Integer code;
    private String ip;//请求来源IP
    private String url;//请求的url
    private String logLevel;//日志级别 INFO 跟 ERROR 报错就是ERROR
    private String httpMethod;//请求类型 GET POST
    private String classMethod;//请求的类中的哪个方法
    private Object requestParams;//传参
    private Object result;//返参，报错无
    private String createTime;//创建时间
    private Long timeCost;//响应时间（只计算请求开始跟结束的时间差，请求结束前会异步入库，不影响响应时间，不考虑这块时间），报错无
    private String exception;//报错内容

    public RequestInfo(String ip, String url, String logLevel, String classMethod, Object requestParams, Object result, String createTime, Long timeCost, Object exception) {
        this.id = UUIDUtil.getUUID();
        this.ip = ip;
        this.code = 200;
        this.url = url;
        this.logLevel = logLevel;
        this.classMethod = classMethod;
        this.requestParams = (requestParams != null && requestParams.toString().length() > 511) ? "传参太长了,只截取了一部分:" + requestParams.toString().substring(0, 500) : requestParams;
        this.result = (result != null && result.toString().length() > 511) ? "返回值太长了,只截取了一部分:" + result.toString().substring(0, 500) : result;
        this.createTime = createTime;
        this.timeCost = timeCost;
        if (exception instanceof Exception) {
            this.exception = ((Exception) exception).getMessage();
        } else if (exception == null) {
            this.exception = null;
        } else {
            this.exception = exception.toString();
        }
    }

    public RequestInfo(String logLevel, Object exception) {
        RequestParam requestParam = (RequestParam) RequestContext.getPrincipal();
        this.id = UUIDUtil.getUUID();
        this.ip = IPUtils.getIpAddr();
        this.code = 500;
        this.url = requestParam.getUrl();
        this.logLevel = logLevel;
        this.classMethod = requestParam.getMethod();
        this.requestParams = requestParam.getParams();
        this.result = requestParam.getResult();
        this.createTime = DateFormatUtil.stampToDate(requestParam.getEnd());
        this.timeCost = requestParam.getTimeCost();
        if (exception instanceof Exception) {
            this.exception = ((Exception) exception).getMessage();
            if (exception instanceof ServiceException) {
                ServiceException se = (ServiceException) exception;
                this.exception = se.getMessage();
                this.code = se.getCode();
            }
        } else if (exception == null) {
            this.exception = null;
        } else {
            this.exception = exception.toString();
        }
    }
}
