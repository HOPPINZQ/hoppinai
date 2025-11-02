package com.hoppinzq.service.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.service.bean.LoginUser;

public class UserUtil {

    public static Long getUserId() {
        JSONObject userJson = getUser();
        if (userJson == null) {
            return null;
        }
        return userJson.getLong("id");
    }

    public static JSONObject getUser() {
        String user = (String) LoginUser.getUserHold();
        return JSON.parseObject(user);
    }
}

