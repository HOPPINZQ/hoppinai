package com.hoppinzq.service.circuitBreaker.cache;

import com.hoppinzq.service.circuitBreaker.bean.CircuitBreakerSetting;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CircuitBreakerCache {
    public static Map<String, CircuitBreakerState> stateMap = new ConcurrentHashMap<>();

    public static void init() {
        stateMap = new ConcurrentHashMap<>();
    }

    public static synchronized CircuitBreakerState getState(String methodName) {
        CircuitBreakerState state = stateMap.get(methodName);
        if (state == null) {
            state = new CircuitBreakerState();
            stateMap.put(methodName, state);
        }
        state.setRequestCount(state.getRequestCount() + 1);
        return state;
    }

    /**
     * 内部类，用于统计
     */
    public static class CircuitBreakerState {
        private String uuid;
        //请求次数
        private int requestCount = 0;
        private int successCount = 0;
        private int failureCount = 0;
        private int failureFuncCount = 0;
        //circuitOpen字段为true表示熔断器处于打开状态，此时请求会被直接拒绝。
        //circuitOpen字段为false时，熔断器处于关闭状态，请求会正常通过熔断器进行处理。
        private boolean circuitOpen = false;
        //半开状态，熔断器不会工作，而且允许请求服务以测试服务是否恢复
        private boolean circuitHalfOpen = false;
        private int openCount = 0;
        private int halfOpenCount = 0;
        //熔断器最大开启时间
        private long maxOpenTime = 0;
        //熔断器上一次关闭时间
        private long lastCloseTime = 0;
        //上一次调用服务时间
        private long lastInvocationTime = 0;
        private CircuitBreakerSetting setting;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public CircuitBreakerSetting getSetting() {
            return setting;
        }

        public void setSetting(CircuitBreakerSetting setting) {
            CircuitBreakerSetting temp = new CircuitBreakerSetting();
            temp.setMethodName(setting.getMethodName());
            temp.setUuid(setting.getUuid());
            this.uuid = setting.getUuid();
            temp.setTimeout(setting.getTimeout());
            temp.setFallback(setting.getFallback());
            temp.setAliveTime(setting.getAliveTime());
            this.setting = temp;
        }

        public long getLastCloseTime() {
            return lastCloseTime;
        }

        public void setLastCloseTime(long lastCloseTime) {
            this.lastCloseTime = lastCloseTime;
        }

        public int getFailureFuncCount() {
            return failureFuncCount;
        }

        public void setFailureFuncCount(int failureFuncCount) {
            this.failureFuncCount = failureFuncCount;
        }

        public int getOpenCount() {
            return openCount;
        }

        public void setOpenCount(int openCount) {
            this.openCount = openCount;
        }

        public int getHalfOpenCount() {
            return halfOpenCount;
        }

        public void setHalfOpenCount(int halfOpenCount) {
            this.halfOpenCount = halfOpenCount;
        }

        public long getMaxOpenTime() {
            return maxOpenTime;
        }

        public void setMaxOpenTime(long maxOpenTime) {
            this.maxOpenTime = maxOpenTime;
        }

        public int getRequestCount() {
            return requestCount;
        }

        public void setRequestCount(int requestCount) {
            this.requestCount = requestCount;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }

        public boolean isCircuitOpen() {
            return circuitOpen;
        }

        public void setCircuitOpen(boolean circuitOpen) {
            this.circuitOpen = circuitOpen;
        }

        public boolean isCircuitHalfOpen() {
            return circuitHalfOpen;
        }

        public void setCircuitHalfOpen(boolean circuitHalfOpen) {
            this.circuitHalfOpen = circuitHalfOpen;
        }

        public long getLastInvocationTime() {
            return lastInvocationTime;
        }

        public void setLastInvocationTime(long lastInvocationTime) {
            this.lastInvocationTime = lastInvocationTime;
        }
    }
}
