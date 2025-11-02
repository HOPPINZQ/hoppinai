package com.hoppinzq.service.loadBalance;

import com.hoppinzq.service.bean.ServiceWrapperRPC;
import lombok.Data;

@Data
public class LoadBalanceServiceWrapper {
    private String id;
    private String serviceName;
    private String ip;
    private String port;
    private String prefix;
    private ServiceWrapperRPC serviceWrapperRPC;
}
