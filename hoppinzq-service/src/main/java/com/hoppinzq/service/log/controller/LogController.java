package com.hoppinzq.service.log.controller;

import com.hoppinzq.service.bean.ApiResponse;
import com.hoppinzq.service.log.cache.LogCache;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author:ZhangQi 日志查询接口
 **/
@RestController
@RequestMapping("/service")
public class LogController {

    @RequestMapping("/showLogs")
    public ApiResponse showLogs() {
        return ApiResponse.data(LogCache.logList);
    }


}
