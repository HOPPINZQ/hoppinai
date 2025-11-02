package com.hoppinzq.service.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author:ZhangQi api网关配置类
 **/
@ConfigurationProperties(prefix = ApiPropertyBean.prefix)
@Data
public class ApiPropertyBean {
    public static final String prefix = "auth";

    private Boolean isAuth;
    private String ssoUrl;
    private String ssoAdminUrl;

    /**
     * 是否允许未登录状态调用（调试用，会在某些需要获取当前登录人的接口中获取不到当前登录人）
     *
     * @return
     */
    public Boolean isAuth() {
        return isAuth;
    }

}
