package com.hoppinzq.service.util;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhangqi
 */
public class StringUtil {
    public static boolean isNotEmpty(Object obj) {
        return obj != null && !"".equals(obj.toString().trim());
    }

    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }

    public static String notNull(String str) {
        return str == null ? "" : str;
    }

    public static List getStaticList(String[] strings) {
        return Arrays.asList(strings);
    }
}

