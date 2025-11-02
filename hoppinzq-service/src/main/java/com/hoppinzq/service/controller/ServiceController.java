package com.hoppinzq.service.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.service.bean.ApiResponse;
import com.hoppinzq.service.bean.ServiceWrapper;
import com.hoppinzq.service.bean.ServiceWrapperRPC;
import com.hoppinzq.service.cache.RPCServiceStore;
import com.hoppinzq.service.cache.ServiceStore;
import com.hoppinzq.service.config.RegisterCoreConfig;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author:ZhangQi 通用接口，可定制
 **/
@RestController
@RequestMapping("/service")
public class ServiceController {

    @RequestMapping("/isCore")
    public ApiResponse isCore() {
        return ApiResponse.data(RegisterCoreConfig.isCore);
    }

    @RequestMapping("/serviceList")
    @ResponseBody
    public ApiResponse getMessage() {
        JSONObject api = new JSONObject();
        //外部服务
        List<ServiceWrapperRPC> outerServiceWrapperList = RPCServiceStore.serviceWrapperRPCList;
        JSONArray jsonArray = new JSONArray();
        for (ServiceWrapperRPC serviceWrapper : outerServiceWrapperList) {
            if (serviceWrapper.getVisible()) {
                jsonArray.add(JSONObject.toJSON(serviceWrapper));
            }
        }
        api.put("outer", jsonArray);
        //内部服务
        List<ServiceWrapper> serviceWrapperList = ServiceStore.serviceWrapperList;
        jsonArray = new JSONArray();
        for (ServiceWrapper serviceWrapper : serviceWrapperList) {
            if (serviceWrapper.isVisible()) {
                jsonArray.add(serviceWrapper.toJSON());
            }
        }
        api.put("inner", jsonArray);
        return ApiResponse.data(api);
    }
}
