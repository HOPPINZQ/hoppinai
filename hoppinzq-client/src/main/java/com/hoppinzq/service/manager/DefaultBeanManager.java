package com.hoppinzq.service.manager;

import java.util.HashMap;
import java.util.Map;

/**
 * @author:ZhangQi 默认bean管理器
 */
public class DefaultBeanManager implements BeanManager {
    private final Map beanMap;

    public DefaultBeanManager() {
        beanMap = new HashMap<>();
    }

    @Override
    public Object getBean(String beanName) {
        return beanMap.get(beanName);
    }

    @Override
    public void registerBean(String beanName, Object bean) {
        beanMap.put(beanName, bean);
    }

    @Override
    public void removeBean(String beanName) {
        beanMap.remove(beanName);
    }
}

