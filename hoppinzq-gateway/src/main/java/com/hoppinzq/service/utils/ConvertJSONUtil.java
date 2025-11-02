package com.hoppinzq.service.utils;

import com.hoppinzq.service.bean.ErrorEnum;
import com.hoppinzq.service.exception.ResultReturnException;
import com.hoppinzq.service.util.JSONUtil;

import java.sql.Date;

/**
 * 工具类
 */
public class ConvertJSONUtil {
    /**
     * 将MAP转换成具体的目标方方法参数对象
     *
     * @param val
     * @param targetClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Object convertJsonToBean(Object val, Class<T> targetClass) throws Exception {
        Object result = null;
        if (val == null) {
            return null;
        } else if (Integer.class.equals(targetClass)) {
            result = Integer.parseInt(val.toString());
        } else if (Long.class.equals(targetClass)) {
            result = Long.parseLong(val.toString());
        } else if (Date.class.equals(targetClass)) {
            if (val.toString().matches("[0-9]+")) {
                result = new Date(Long.parseLong(val.toString()));
            } else {
                throw new ResultReturnException(ErrorEnum.COMMON_DATE_MUST_TIMESTAMP);
            }
        } else if (String.class.equals(targetClass)) {
            if (val instanceof String) {
                result = val;
            } else {
                throw new ResultReturnException(ErrorEnum.COMMON_DATE_TARGET_MUST_STRING);
            }
        } else {
            result = JSONUtil.convertValue(val, targetClass);
        }
        return result;
    }
}

