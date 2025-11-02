package com.hoppinzq.dto;

import com.hoppinzq.dal.po.ChatMessagePO;
import com.hoppinzq.dal.po.ChatPO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InsertChatDTO extends ChatPO {

    private List<ChatMessagePO> chatMessageList;
}

