package com.hoppinzq.service.auth;

import java.io.Serializable;

/**
 * @author:ZhangQi 匿名主体类，游客用户的功能
 */
public class Anonymous implements Serializable {
    private static final long serialVersionUID = 2783377098145241357L;

    public String getName() {
        return getClass().getCanonicalName();
    }
}
