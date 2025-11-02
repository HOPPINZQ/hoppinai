package com.hoppinzq.service.manager;

/**
 * @author:ZhangQi bean管理器接口
 */
public interface BeanManager {
    Object getBean(String beanName);

    void registerBean(String beanName, Object bean);

    void removeBean(String beanName);
}