package com.hoppinzq.a2a.client;

import com.hoppinzq.a2a.model.AgentCard;

import java.util.List;

/**
 * Example usage of A2A client - AI Translation Bot
 */
public class A2AClientExample3 {

    public static void main(String[] args) {
        // Create client
        A2AClient client = new A2AClient("http://localhost:9003");

        try {
            System.out.println("=== 获取代理卡 ===");
            List<AgentCard> agentCards = client.getAgentCards();
            for (AgentCard card : agentCards) {
                System.out.println("智能体: " + card.name());
                System.out.println("描述: " + card.description());
                System.out.println("版本: " + card.version());
                System.out.println("技能: " + card.skills());
                System.out.println();
            }
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
