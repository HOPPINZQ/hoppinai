package com.hoppinzq.service.circuitBreaker.cache;

import com.hoppinzq.service.circuitBreaker.bean.CircuitBreakerSetting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CircuitBreakerStore {
    public static List<CircuitBreakerSetting> circuitBreakerSettings = Collections.synchronizedList(new ArrayList<CircuitBreakerSetting>());

}
