package com.hoppinzq.service.bean;

/**
 * @author:ZhangQi 带有优先级排序的类
 */
public class OrderServiceClazz {
    private int order;
    private Class clazz;
    private String className;
    private int type;

    public OrderServiceClazz(int order, Class clazz, OrderServiceTypeEnum orderServiceTypeEnum, String className) {
        this.order = order;
        this.clazz = clazz;
        this.type = orderServiceTypeEnum.getState();
        this.className = className;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public enum OrderServiceTypeEnum {
        INTERCEPTOR(1, "拦截器"),
        FILTER(2, "过滤器"),
        THROWABLE(3, "异常拦截器");
        private final int state;
        private final String info;

        OrderServiceTypeEnum(int state, String info) {
            this.state = state;
            this.info = info;
        }

        public int getState() {
            return state;
        }
    }
}
