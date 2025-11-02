package com.hoppinzq.service.chatgpt;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hoppinzq.dal.dao.ChatMapper;
import com.hoppinzq.dal.dao.ChatMessageMapper;
import com.hoppinzq.dal.po.ChatMessagePO;
import com.hoppinzq.dal.po.ChatPO;
import com.hoppinzq.dto.InsertChatDTO;
import com.hoppinzq.dto.TokenRequestDTO;
import com.hoppinzq.model.exception.OpenaiException;
import com.hoppinzq.model.openai.Usage;
import com.hoppinzq.openai.util.TikTokensUtil;
import com.hoppinzq.query.LambdaQueryWrapperX;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.util.UUIDUtil;
import com.hoppinzq.service.util.object.BeanUtils;
import com.hoppinzq.service.utils.UserUtil;
import com.knuddels.jtokkit.api.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@ApiServiceMapping(title = "gpt对话", roleType = ApiServiceMapping.RoleType.RIGHT)
public class GPTChatService {

    @Autowired
    private ChatMapper chatMapper;
    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @ApiMapping(value = "getChatId", title = "获取一个ID", description = "获取一个ID",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public String getId() {
        return UUIDUtil.getUUID();
    }

    @ApiMapping(value = "insertChatFirst", title = "新增聊天", description = "新增聊天",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    @Transactional(rollbackFor = Exception.class)
    public void insertChatFirst(InsertChatDTO chat) {
        chat.setChat_user_id(String.valueOf(UserUtil.getUserId()));
        ChatPO chatPO = BeanUtils.toBean(chat, ChatPO.class);
        chatMapper.insert(chatPO);
        List<ChatMessagePO> chatMessageList = chat.getChatMessageList();
        for (int i = 0; i < chatMessageList.size(); i++) {
            ChatMessagePO chatMessagePO = chatMessageList.get(i);
            chatMessagePO.setChat_id(chatPO.getChat_id());
            chatMessageMapper.insert(chatMessagePO);
        }
    }

    @ApiMapping(value = "insertChatMessage", title = "新增对话", description = "新增对话",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    @Transactional(rollbackFor = Exception.class)
    public void insertChatMessage(InsertChatDTO chat) {
        List<ChatMessagePO> chatMessageList = chat.getChatMessageList();
        for (int i = 0; i < chatMessageList.size(); i++) {
            ChatMessagePO chatMessagePO = chatMessageList.get(i);
            chatMessagePO.setChat_id(chat.getChat_id());
            chatMessageMapper.insert(chatMessagePO);
        }
    }

    @ApiMapping(value = "updateChat", title = "编辑对话", description = "编辑对话",
            roleType = ApiMapping.RoleType.LOGIN, type = ApiMapping.Type.POST)
    public void insertOrUpdateChat(ChatPO chat) {
        chat.setChat_user_id(String.valueOf(UserUtil.getUserId()));
        chatMapper.updateById(chat);
    }

    @Async
    public void insertChatMessage(ChatMessagePO chatMessage) {
        chatMessageMapper.insert(chatMessage);
    }

    @Async
    public void insertSM(List<String> sm) {
        chatMessageMapper.isms(sm);
    }

    public void insertChatMessage(List<Map> chatMessages) {
        List<String> xl = new ArrayList<>();
        for (int i = 0; i < chatMessages.size(); i++) {
            Map map = chatMessages.get(i);
            ChatMessagePO chatMessage = new ChatMessagePO().toMap(map);
            chatMessageMapper.insert(chatMessage);
            xl.add(chatMessage.getMessage());
        }
        this.insertSM(xl);
    }

    public void updateChatMessages(List<Map> chatMessages) {
        List<String> xl = new ArrayList<>();
        for (int i = 0; i < chatMessages.size(); i++) {
            Map map = chatMessages.get(i);
            ChatMessagePO chatMessage = new ChatMessagePO().toMap(map);
            chatMessageMapper.updateById(chatMessage);
        }
    }

    public ChatPO getChatByChatId(String chatId) {
        return chatMapper.selectById(chatId);
    }

    public List<ChatPO> getPublicChats() {
        return chatMapper.selectList(new LambdaQueryWrapperX<ChatPO>()
                .eq(ChatPO::getChat_state, 1));
    }


    public List<ChatMessagePO> getChatMessageByChatId(String chatId, Integer number) {
        return chatMessageMapper.selectList(new LambdaQueryWrapperX<ChatMessagePO>()
                .eq(ChatMessagePO::getChat_id, chatId)
                .orderByDesc(ChatMessagePO::getMessage_index)
                .orderByAsc(ChatMessagePO::getMessage_role)
                .last("LIMIT " + number)
        );
    }

    @ApiMapping(value = "getChatAndMessageByUserId", title = "获取聊天以及对话", description = "获取聊天对话通过userId",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public List<ChatPO> getChatAndMessageByUserId(String userId) {
        List<ChatPO> chatList = chatMapper.queryChat(userId);
        return chatList;
    }

    @ApiMapping(value = "getChatByUserId", title = "只获取聊天", description = "只获取聊天通过userId",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public List<ChatPO> getChatByUserId(String userId) {
        List<ChatPO> chatList = chatMapper.selectList(new LambdaQueryWrapper<ChatPO>().eq(ChatPO::getChat_user_id, userId));
        return chatList;
    }

    @ApiMapping(value = "deleteChatByChatId", title = "删除对话", description = "连聊天也会删除",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    @Transactional(rollbackFor = Exception.class)
    public void deleteChatByChatId(String chatId) {
        chatMapper.deleteById(chatId);
        chatMessageMapper.delete(new LambdaQueryWrapperX<ChatMessagePO>()
                .eq(ChatMessagePO::getChat_id, chatId));
    }

    @ApiMapping(value = "calculateToken", title = "计算token", description = "计算token",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.POST)
    public Usage calculateToken(TokenRequestDTO tokenRequestDTO) {
        ModelType modelTypeByName = TikTokensUtil.getModelTypeByName(tokenRequestDTO.getModel());
        if (modelTypeByName == null) {
            Set<String> modelName = TikTokensUtil.getModelName();
            throw new OpenaiException("不支持该模型：" + tokenRequestDTO.getModel() + "，目前只支持：" + modelName);
        } else {
            int tokens_1 = TikTokensUtil.tokens(modelTypeByName.getName(), tokenRequestDTO.getMessages());
            int tokens_2 = TikTokensUtil.tokens(modelTypeByName.getName(), tokenRequestDTO.getMessage());
            Usage usage = new Usage();
            usage.setPromptTokens(tokens_1);
            usage.setCompletionTokens(tokens_2);
            usage.setTotalTokens(tokens_1 + tokens_2);
            return usage;
        }
    }

    public void deleteChatMessageByMessageId(String messageId) {
        chatMessageMapper.deleteById(messageId);
    }

    @ApiMapping(value = "deleteChatMessageBatchByMessageIds", title = "删除对话", description = "删除对话",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public void deleteChatMessageBatchByMessageIds(List<String> messageIds) {
        chatMessageMapper.deleteBatchIds(messageIds);
    }

    @ApiMapping(value = "deleteChatMessageBatchRecent", title = "删除最近两条对话", description = "删除最近两条对话",
            roleType = ApiMapping.RoleType.NO_RIGHT, type = ApiMapping.Type.GET)
    public void deleteChatMessageBatchRecent(String chatId) {
        List<ChatMessagePO> chatMessagePOS = chatMessageMapper.selectList(new LambdaQueryWrapperX<ChatMessagePO>()
                .eq(ChatMessagePO::getChat_id, chatId).orderByDesc(ChatMessagePO::getMessage_index).last("LIMIT 2"));
        chatMessageMapper.deleteBatchIds(chatMessagePOS.stream().map(ChatMessagePO::getMessage_id).collect(Collectors.toList()));
    }
}

