package com.hoppinzq.a2a.client;

import com.hoppinzq.a2a.model.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Example usage of A2A client - AI Translation Bot
 */
public class A2AClientExample {

    public static void main(String[] args) {
        // Create client
        A2AClient client = new A2AClient("http://localhost:9003");

        try {
            // Example 1: Get agent card
            System.out.println("=== 获取翻译机器人的代理卡 ===");
            AgentCard agentCard = client.getAgentCard();
            System.out.println("智能体: " + agentCard.name());
            System.out.println("描述: " + agentCard.description());
            System.out.println("版本: " + agentCard.version());
            System.out.println("技能: " + agentCard.skills());
            System.out.println();

            // Example 2: Translate French to Chinese
            System.out.println("=== 将法语翻译为汉语 ===");

            // Create text part for French to Chinese translation
            TextPart frenchToChinesePart = new TextPart("Bonjour le monde! Comment allez-vous?", null);

            Message frenchToChineseMessage = new Message(
                    UUID.randomUUID().toString(),  // messageId
                    "message",                     // kind
                    "user",                        // role
                    List.of(frenchToChinesePart), // parts
                    null,                         // contextId
                    null,                         // taskId
                    null,                         // referenceTaskIds
                    null                          // metadata
            );

            TaskSendParams frenchToChineseParams = new TaskSendParams(
                    "french-to-chinese-task",
                    null,  // sessionId
                    frenchToChineseMessage,
                    null,  // pushNotification
                    null,  // historyLength
                    Map.of()  // metadata
            );

            JSONRPCResponse frenchToChineseResponse = client.sendTask(frenchToChineseParams);
            Task frenchToChineseTask = (Task) frenchToChineseResponse.result();
            System.out.println("初始的法语: " + frenchToChinesePart.text());
            System.out.println("任务ID: " + frenchToChineseTask.id());
            System.out.println("任务状态: " + frenchToChineseTask.status().state());

            // Print translation result if available in history
            if (frenchToChineseTask.history() != null && frenchToChineseTask.history().size() > 1) {
                Message lastMessage = frenchToChineseTask.history().get(frenchToChineseTask.history().size() - 1);
                if (lastMessage.role().equals("assistant") && !lastMessage.parts().isEmpty()) {
                    Part translationPart = lastMessage.parts().get(0);
                    if (translationPart instanceof TextPart textPart) {
                        System.out.println("中文翻译: " + textPart.text());
                    }
                }
            }
            System.out.println();

            // Example 3: Translate Chinese to English
            System.out.println("=== 将中文翻译为英语 ===");

            TextPart chineseTextPart = new TextPart("你好，世界！欢迎使用AI翻译机器人。", null);

            Message chineseMessage = new Message(
                    UUID.randomUUID().toString(),  // messageId
                    "message",                     // kind
                    "user",                        // role
                    List.of(chineseTextPart),     // parts
                    null,                         // contextId
                    null,                         // taskId
                    null,                         // referenceTaskIds
                    null                          // metadata
            );

            TaskSendParams chineseParams = new TaskSendParams(
                    "chinese-to-english-task",
                    null,  // sessionId
                    chineseMessage,
                    null,  // pushNotification
                    null,  // historyLength
                    Map.of()  // metadata
            );

            JSONRPCResponse chineseResponse = client.sendTask(chineseParams);
            Task chineseTask = (Task) chineseResponse.result();
            System.out.println("初始中文: " + chineseTextPart.text());
            System.out.println("任务ID: " + chineseTask.id());
            System.out.println("任务状态: " + chineseTask.status().state());

            // Print translation result if available in history
            if (chineseTask.history() != null && chineseTask.history().size() > 1) {
                Message lastMessage = chineseTask.history().get(chineseTask.history().size() - 1);
                if (lastMessage.role().equals("assistant") && !lastMessage.parts().isEmpty()) {
                    Part translationPart = lastMessage.parts().get(0);
                    if (translationPart instanceof TextPart textPart) {
                        System.out.println("英文翻译: " + textPart.text());
                    }
                }
            }
            System.out.println();

            // Example 4: Translate with streaming (French to English)
            System.out.println("=== 流式翻译 (法语翻译为英语) ===");

            TextPart frenchTextPart = new TextPart("Bonjour le monde! Comment allez-vous?", null);
            Message frenchMessage = new Message(
                    UUID.randomUUID().toString(),
                    "message",
                    "user",
                    List.of(frenchTextPart),
                    null, null, null, null
            );

            TaskSendParams frenchParams = new TaskSendParams(
                    "french-streaming-task",
                    null,  // sessionId
                    frenchMessage,
                    null,  // pushNotification
                    null,  // historyLength
                    Map.of()  // metadata
            );

            CountDownLatch streamingLatch = new CountDownLatch(1);
            System.out.println("初始的法语: " + frenchTextPart.text());

            client.sendTaskStreaming(frenchParams, new StreamingEventListener() {
                @Override
                public void onEvent(Object event) {
                    System.out.println("流事件: " + event);
                }

                @Override
                public void onError(Exception exception) {
                    System.err.println("错误: " + exception.getMessage());
                    streamingLatch.countDown();
                }

                @Override
                public void onComplete() {
                    System.out.println("翻译完成");
                    streamingLatch.countDown();
                }
            });

            // Wait for streaming to complete
            if (streamingLatch.await(30, TimeUnit.SECONDS)) {
                System.out.println("流成功结束");
            } else {
                System.out.println("流超时");
            }
            System.out.println();

            // Example 5: Get task status for translation
            System.out.println("=== 获取任务状态 ===");
            TaskQueryParams queryParams = new TaskQueryParams(frenchToChineseTask.id(), Map.of(), null);
            JSONRPCResponse getResponse = client.getTask(queryParams);
            Task retrievedTask = (Task) getResponse.result();
            System.out.println("回收翻译任务: " + retrievedTask.id());
            System.out.println("最终状态: " + retrievedTask.status().state());
            System.out.println();

            // Example 6: Cancel a translation task
            System.out.println("=== 取消翻译任务 ===");

            TextPart cancelTextPart = new TextPart("Diese Übersetzung wird abgebrochen.", null); // German
            Message cancelMessage = new Message(
                    UUID.randomUUID().toString(),
                    "message",
                    "user",
                    List.of(cancelTextPart),
                    null, null, null, null
            );

            TaskSendParams cancelParams = new TaskSendParams(
                    "german-cancel-task",
                    null,  // sessionId
                    cancelMessage,
                    null,  // pushNotification
                    null,  // historyLength
                    Map.of()  // metadata
            );

            // Send task to be canceled
            JSONRPCResponse cancelResponse = client.sendTask(cancelParams);
            Task cancelTask = (Task) cancelResponse.result();
            System.out.println("德语翻译: " + cancelTextPart.text());
            System.out.println("翻译任务取消: " + cancelTask.id());

            // Cancel the task
            TaskIDParams cancelTaskParams = new TaskIDParams(cancelTask.id(), Map.of());
            JSONRPCResponse cancelResult = client.cancelTask(cancelTaskParams);
            Task canceledTask = (Task) cancelResult.result();
            System.out.println("任务取消: " + canceledTask.id());
            System.out.println("最终状态: " + canceledTask.status().state());

        } catch (A2AClientException e) {
            System.err.println("A2A Client错误: " + e.getMessage());
            if (e.getErrorCode() != null) {
                System.err.println("错误编码: " + e.getErrorCode());
            }
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
