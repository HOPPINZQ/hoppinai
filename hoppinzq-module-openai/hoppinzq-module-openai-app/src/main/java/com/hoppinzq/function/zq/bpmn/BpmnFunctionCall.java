package com.hoppinzq.function.zq.bpmn;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BpmnFunctionCall {

    public String msg;
    public Boolean success = false;

    public JSONObject startProcess(String processName, String days, String reason, String assigneeUser) {
        // 这里可以调用实际的流程启动逻辑
        System.out.println("流程名：" + processName);
        System.out.println("请假天数：" + days);
        System.out.println("请假原因：" + reason);
        System.out.println("下一节点任务负责人：" + assigneeUser);
        this.success = true;
        this.msg = "流程启动成功";
        JSONObject result = new JSONObject();
        result.put("success", this.success);
        result.put("msg", this.msg);
        return result;
    }

    public static class StartProcessFunctionCallRequest {
        @JsonPropertyDescription("流程名")
        @JsonProperty(required = true)
        public String processName;

        @JsonPropertyDescription("请假天数")
        @JsonProperty(required = true)
        public String days;

        @JsonPropertyDescription("请假原因")
        public String reason;

        @JsonPropertyDescription("下一节点任务负责人")
        public String assigneeUser;
    }

    public static class showProcessInstListFunctionCallRequest {

        @JsonPropertyDescription("用户名")
        @JsonProperty(required = true)
        public String userName;
    }

    public static class handleProcessInstListFunctionCallRequest {

        @JsonPropertyDescription("流程实例ID")
        @JsonProperty(required = true)
        public String procInstId;

        @JsonPropertyDescription("任务ID")
        @JsonProperty(required = true)
        public String taskId;

        @JsonPropertyDescription("意见")
        public String option;

        @JsonPropertyDescription("是否同意审批")
        @JsonProperty(required = true)
        public Boolean isOk;
    }


}

