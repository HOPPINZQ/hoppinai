package com.hoppinzq.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 登录日志的类型枚举
 */
@Getter
@AllArgsConstructor
public enum LoginTypeEnum {

    LOGIN_USERNAME("hoppinzq"), // 使用账号登录
    LOGIN_SMS("sms"), // 使用短信登陆
    LOGIN_WYY("wyy"), // 网易云账号
    LOGIN_GITEE("gitee"), // gitee
    LOGIN_WEIBO("weibo"), // weibo
    LOGIN_QQ("qq"), // qq
    LOGIN_GITHUB("github"), // qq
    ;

    /**
     * 日志类型
     */
    private final String type;

}
