package com.hoppinzq.service.bean;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author zhangqi
 * rpc参数的配置映射的实体类
 */
@Component
@Scope("singleton")
//@ConfigurationProperties("zqClient")
public class RPCPropertyBean {

    @Value("${zqClient.centerAddr:http://127.0.0.1:8801/service}")
    private String serverCenter;
    @Value("${zqClient.authAddr:http://127.0.0.1:8804/service}")
    private String serverAuth;

    @Value("${zqClient.userName:zhangqi}")
    private String userName;

    @Value("${zqClient.password:123456}")
    private String password;

    public String getServerCenter() {
        return serverCenter;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getServerAuth() {
        return serverAuth;
    }
}
