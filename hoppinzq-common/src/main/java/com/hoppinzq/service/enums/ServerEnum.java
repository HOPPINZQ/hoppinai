package com.hoppinzq.service.enums;

/**
 * @author: ZhangQi
 * 注册中心服务类型枚举
 */
public enum ServerEnum {

    INNER(0, "内部服务"),
    OUTER(1, "外部服务");

    private final int state;
    private final String info;

    ServerEnum(int state, String info) {
        this.state = state;
        this.info = info;
    }

    public static ServerEnum stateOf(int index) {
        for (ServerEnum state : values()) {
            if (state.getState() == index) {
                return state;
            }
        }
        return null;
    }

    public int getState() {
        return state;
    }

    public String getInfo() {
        return info;
    }
}
