package com.hoppinzq.service.log.cache;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author:ZhangQi 调用日志缓存
 */
public class LogCache {
    public static ConcurrentHashMap<String, String> logMap = new ConcurrentHashMap<>();
    public static List<String> logList = Collections.synchronizedList(new ArrayList<>());
}