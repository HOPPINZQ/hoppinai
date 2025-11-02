package com.hoppinzq.red95;

import cn.hutool.core.lang.UUID;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hoppinzq.red95.model.AgentCard;
import com.hoppinzq.red95.model.JSONRPCRequest;
import com.hoppinzq.red95.model.JSONRPCResponse;
import com.hoppinzq.red95.service.Red95Service;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Red95Main {

    public static void main(String[] args) throws InterruptedException {
        Red95Service red95Service = new Red95Service("red95");
        AgentCard agentCard = red95Service.getAgentCard();
        System.out.println(JSON.toJSONString(agentCard));

        JSONRPCRequest sendTaskRequest = JSONRPCRequest.builder()
                .id(UUID.fastUUID().toString())
                .jsonrpc("2.0")
                .method("tasks/send")
                .params(JSON.parseObject("{\"id\":\"" + UUID.fastUUID() + "\",\"message\":{\"messageId\":\"msg-1\",\"kind\":\"message\",\"role\":\"user\",\"parts\":[{\"kind\":\"text\",\"text\":\"Hello, World!\"}]}}"))
                .build();

        JSONRPCResponse sendTaskResponse = red95Service.task(sendTaskRequest);
        System.out.println(JSON.toJSONString(sendTaskResponse));
        if (sendTaskResponse.getError() != null) {
            System.err.println("发送任务错误: " + sendTaskResponse.getError().getMessage());
        } else {
            String result = JSON.toJSONString(sendTaskResponse.getResult());
            String taskId = JSONObject.parseObject(result).getString("id");
            JSONRPCResponse jsonrpcResponse = red95Service.task(JSONRPCRequest.builder()
                    .id(UUID.fastUUID().toString())
                    .jsonrpc("2.0").method("tasks/get")
                    .params(JSON.parseObject("{\"id\":\"" + taskId + "\"}"))
                    .build());
            System.out.println(JSON.toJSONString(jsonrpcResponse));
        }

        System.out.println("-----------------------------------------");
        CountDownLatch streamingLatch = new CountDownLatch(1);
        red95Service.taskStream(sendTaskRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnError(msg -> {
                    System.err.println("发送任务错误: " + msg);
                    red95Service.shutdownExecutor();
                    streamingLatch.countDown();
                })
                .doOnCancel(() -> {
                    System.out.println("取消订阅");
                    red95Service.shutdownExecutor();
                    streamingLatch.countDown();
                })
                .subscribe(streamResponse -> {
                    System.out.println("接收到消息: " + streamResponse);
                    JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(streamResponse.getResult()));
                    String state = jsonObject.getJSONObject("status").getString("state");
                    if (!"completed".equals(state)) {
                        System.err.println("任务未完成，状态: " + state);
                        return;
                    } else {
                        System.out.println("任务已完成，状态: " + state);
                    }
                    String taskId = jsonObject.getString("id");
                    JSONRPCResponse jsonrpcResponse = red95Service.task(JSONRPCRequest.builder()
                            .id(UUID.fastUUID().toString())
                            .jsonrpc("2.0").method("tasks/get")
                            .params(JSON.parseObject("{\"id\":\"" + taskId + "\"}"))
                            .build());
                    System.out.println(JSON.toJSONString(jsonrpcResponse));
                }, err -> {
                    System.err.println("接收消息错误: " + err);
                }, () -> {
                    System.out.println("订阅完成");
                    red95Service.shutdownExecutor();
                    streamingLatch.countDown();
                });
        if (streamingLatch.await(10, TimeUnit.SECONDS)) {
            System.out.println("流成功结束");
        } else {
            System.err.println("流超时");
        }
    }
}

