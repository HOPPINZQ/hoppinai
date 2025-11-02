package com.hoppinzq.service.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author:ZhangQi 一批配置类
 **/
@Component
@Scope("singleton")
public class ZqServerConfig {

    private String id;

    @Value("${zq-server.name:hoppinzq-core}")
    private String name;

    @Value("${server.port:8080}")
    private String port;

    @Value("${zq-server.prefix:/service}")
    private String prefix;

    @Value("${zq-server.ip:127.0.0.1}")
    private String ip;

    @Value("${zq-server.username:zhangqi}")
    private String userName;

    @Value("${zq-server.password:123456}")
    private String password;

    @Value("${zq-server.secret-id:zhangqi}")
    private String secretId;

    @Value("${zq-server.secret-key:123456}")
    private String secretKey;

    @Value("${zq-server.center-addr:null}")
    private String serverCenter;

    @Value("${zq-server.always-retry:false}")
    private Boolean alwaysRetry;

    @Value("${zq-server.retry-count:10}")
    private int retryCount;

    @Value("${zq-server.retry-time:60000}")
    private int retryTime;

    @Value("${zq-server.show-center:true}")
    private boolean showCenter;

    @Value("${zq-server.file-addr:null}")
    private String fileAddr;

    public boolean isShowCenter() {
        return showCenter;
    }

    public void setShowCenter(boolean showCenter) {
        this.showCenter = showCenter;
    }

    public Boolean getAlwaysRetry() {
        return alwaysRetry;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public int getRetryTime() {
        return retryTime;
    }

    public String getServerCenter() {
        return serverCenter;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
//        String os = System.getProperty("os.name").toLowerCase();
//        if(os.contains("windows")) {
//            ip = IPUtils.getIpAddress();
//        }else{
//            if("127.0.0.1".equals(ip)){
//                ip= IPUtils.getIpAddress();
//            }
//        }
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getFileAddr() {
        return fileAddr;
    }

    public void setFileAddr(String fileAddr) {
        this.fileAddr = fileAddr;
    }
}
