package com.hoppinzq.mcp.service;

import okhttp3.*;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class BpmnService {

    private static final String cookie = "JSESSIONID=32E755A49AF7C7A27796B350477F6B16"; // 先写死了

    @Tool(name = "get_process_def", description = "获取所有流程定义")
    public String getProcessDef() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("http://localhost:8080/MVN_AM_war/zqdef/list.do")
                    .method("POST", body)
                    .addHeader("Cookie", cookie)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception exception) {
            return "获取所有流程定义失败，原因是：" + exception.getMessage();
        }
    }

    @Tool(name = "start_process", description = "开启请假流程，在开启流程前，你需要调用get_process_def获取发起流程的流程定义Key，KEY_")
    public String startProcess(@ToolParam(description = "流程实例Key，通过get_process_def获取") String processKey,
                               @ToolParam(description = "下一个节点负责人") String assignUser,
                               @ToolParam(description = "请假天数，默认1天", required = false) Integer dayNum,
                               @ToolParam(description = "理由，默认事假", required = false) String reason) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(String.format("http://localhost:8080/MVN_AM_war/zqleave/leave.do?processInstanceKey=%s&whenlong=%s&assigneeUser=%s&reason=%s", processKey, dayNum, assignUser, reason))
                    .method("POST", body)
                    .addHeader("Cookie", cookie)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception exception) {
            return "发起流程失败，原因是：" + exception.getMessage();
        }
    }

    @Tool(name = "get_todo_task", description = "获取待办任务")
    public String getTodoTask() {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url("http://localhost:8080/MVN_AM_war/zqhandle/list.do")
                    .method("POST", body)
                    .addHeader("Cookie", cookie)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception exception) {
            return "获取待办任务失败，原因是：" + exception.getMessage();
        }
    }

    @Tool(name = "handle_task", description = "待办流程处理，待办任务需要通过get_todo_task工具获取")
    public String handleTask(@ToolParam(description = "流程实例ID") String procInstId,
                             @ToolParam(description = "任务ID") String taskId,
                             @ToolParam(description = "审批是否通过，请假流程的天数大于或者等于3天审批不通过，小于3天审批通过") Boolean isOk,
                             @ToolParam(description = "审批意见") String option) {
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            MediaType mediaType = MediaType.parse("text/plain");
            RequestBody body = RequestBody.create(mediaType, "");
            Request request = new Request.Builder()
                    .url(String.format("http://localhost:8080/MVN_AM_war/zqhandle/handle.do?procInstId=%s&taskId=%s&option=%s&isOk=%s", procInstId, taskId, option, isOk))
                    .method("POST", body)
                    .addHeader("Cookie", cookie)
                    .build();
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception exception) {
            return "待办流程处理失败，原因是：" + exception.getMessage();
        }
    }
}
