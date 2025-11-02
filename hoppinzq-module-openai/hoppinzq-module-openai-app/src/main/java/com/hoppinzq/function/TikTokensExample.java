package com.hoppinzq.function;

import com.hoppinzq.model.openai.completion.chat.ChatMessage;
import com.hoppinzq.model.openai.completion.chat.ChatMessageRole;
import com.hoppinzq.openai.util.TikTokensUtil;
import com.knuddels.jtokkit.api.ModelType;

import java.util.ArrayList;
import java.util.List;

class TikTokensExample {

    public static void main(String... args) {
        System.out.println("Hello, TikTokens!" + TikTokensUtil.getModelName());
        ModelType modelTypeByName1 = TikTokensUtil.getModelTypeByName("davinci");
        ModelType modelTypeByName2 = TikTokensUtil.getModelTypeByName("gpt-4-0314");
        ModelType modelTypeByName3 = TikTokensUtil.getModelTypeByName("gpt-3.5-turbo-16k");
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage(ChatMessageRole.SYSTEM.value(), ""));
        messages.add(new ChatMessage(ChatMessageRole.USER.value(), "什么是jwt"));

        int tokens_1 = TikTokensUtil.tokens(TikTokensUtil.ModelEnum.GPT_4_1106_preview.getName(), "什么是jwt");
        int tokens_2 = TikTokensUtil.tokens(TikTokensUtil.ModelEnum.GPT_4_1106_preview.getName(), "JWT（JSON Web Token）是一种基于JSON的轻量级、安全的令牌标准。它定义了一种紧凑且自包含的方式来在不同系统之间安全地传递信息。JWT通常用于身份验证和授权，其中包含了对用户身份的一些声明信息，如用户ID、角色等。JWT由三部分组成：头部（header）、载荷（payload）和签名（signature）。头部包含了令牌类型和使用的加密算法等信息，载荷包含了实际传输的用户信息，签名则由头部、载荷和密钥计算而得，用于验证令牌的完整性和真实性。");

        System.out.println("token1:" + tokens_1);
        System.out.println("token2:" + tokens_2);
    }

}
