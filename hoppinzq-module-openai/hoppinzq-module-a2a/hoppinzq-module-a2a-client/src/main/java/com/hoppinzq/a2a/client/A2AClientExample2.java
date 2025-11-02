package com.hoppinzq.a2a.client;

import com.hoppinzq.a2a.model.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Example usage of A2A client - AI Translation Bot
 */
public class A2AClientExample2 {

    public static void main(String[] args) {
        // Create client
        A2AClient client = new A2AClient("http://localhost:9003");

        try {
            System.out.println("=== 获取代理卡 ===");
            AgentCard agentCard = client.getAgentCard();
            System.out.println("智能体: " + agentCard.name());
            System.out.println("描述: " + agentCard.description());
            System.out.println("版本: " + agentCard.version());
            System.out.println("技能: " + agentCard.skills());
            System.out.println();

            System.out.println("=== 搜索音乐 ===");
            TextPart searchMusicPart = new TextPart("AI，帮我搜索一首音乐烟花易冷", null);

            Message searchMusicMessage = new Message(
                    UUID.randomUUID().toString(),  // messageId
                    "message",                     // kind
                    "user",                        // role
                    List.of(searchMusicPart), // parts
                    null,                         // contextId
                    null,                         // taskId
                    null,                         // referenceTaskIds
                    null                          // metadata
            );

            TaskSendParams searchMusicParams = new TaskSendParams(
                    "search-music-task",
                    null,  // sessionId
                    searchMusicMessage,
                    null,  // pushNotification
                    null,  // historyLength
                    Map.of()  // metadata
            );

            JSONRPCResponse searchMusicResponse = client.sendTask(searchMusicParams);
            Task searchMusicTask = (Task) searchMusicResponse.result();
            System.out.println("初始: " + searchMusicPart.text());
            System.out.println("任务ID: " + searchMusicTask.id());
            System.out.println("任务状态: " + searchMusicTask.status().state());

            // Print translation result if available in history
            if (searchMusicTask.history() != null && searchMusicTask.history().size() > 1) {
                Message lastMessage = searchMusicTask.history().get(searchMusicTask.history().size() - 1);
                if (lastMessage.role().equals("assistant") && !lastMessage.parts().isEmpty()) {
                    Part translationPart = lastMessage.parts().get(0);
                    if (translationPart instanceof TextPart textPart) {
                        System.out.println("结果: " + textPart.text());
                    }
                }
            }
            System.out.println();
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
