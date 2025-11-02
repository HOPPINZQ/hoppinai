package com.hoppinzq.service.modification;

import com.hoppinzq.service.common.ModificationList;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * @author:ZhangQi 在set方法前织入代码
 * MethodInterceptor 接口是 Spring AOP 中定义的一个拦截器接口，它定义了一个方法 invoke，该方法在目标方法被调用时被触发。
 */
public class SetterModificationInterceptor implements MethodInterceptor, Serializable {
    //父级路径(因为类的属性中很可能还有类)
    private final String parentPath;
    //记录属性的修改
    private final ModificationList modificationList;

    /**
     * @param parentPath       父级路径
     * @param modificationList 记录属性的修改
     */
    public SetterModificationInterceptor(String parentPath, ModificationList modificationList) {
        this.parentPath = parentPath;
        this.modificationList = modificationList;
    }

    /**
     * 从方法名中提取属性名，只会提取set方法
     *
     * @param method
     * @param args
     * @return
     */
    private String getPropertyName(Method method, Object[] args) {
        if (method.getName().startsWith("set") && args.length == 1) {
            String propertyName = method.getName().substring(3);
            return new String(new char[]{propertyName.charAt(0)}).toLowerCase() + propertyName.substring(1);
        }

        return null;
    }

    /**
     * 在调用set方法前
     *
     * @param methodInvocation
     * @return
     * @throws Throwable
     */
    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        String propertyName = getPropertyName(methodInvocation.getMethod(), methodInvocation.getArguments());
        if (propertyName != null) {
            modificationList.addModification(("".equals(parentPath) ? "" : parentPath + ".") + propertyName, methodInvocation.getArguments()[0]);
        }
        //方法执行前先调用上面方法获取参数
        return methodInvocation.getMethod().invoke(methodInvocation.getThis(), methodInvocation.getArguments());
    }
}