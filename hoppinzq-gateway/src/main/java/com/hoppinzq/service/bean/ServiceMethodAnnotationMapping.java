package com.hoppinzq.service.bean;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 注解参数
 *
 * @author:ZhangQi
 */
public class ServiceMethodAnnotationMapping {
    private String annotationName;
    private Type annotation;
    private List<Map> mapping;
    private boolean isCustom = false;

    public boolean isCustom() {
        return isCustom;
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public String getAnnotationName() {
        return annotationName;
    }

    public void setAnnotationName(String annotationName) {
        this.annotationName = annotationName;
    }

    public Type getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Type annotation) {
        this.annotation = annotation;
    }

    public List<Map> getMapping() {
        return mapping;
    }

    public void setMapping(List<Map> mapping) {
        this.mapping = mapping;
    }
}

