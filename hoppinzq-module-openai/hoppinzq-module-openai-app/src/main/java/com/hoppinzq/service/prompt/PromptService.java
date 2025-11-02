package com.hoppinzq.service.prompt;

import com.hoppinzq.dal.dao.PromptMapper;
import com.hoppinzq.dal.po.PromptPO;
import com.hoppinzq.model.openai.completion.chat.ChatMessage;
import com.hoppinzq.openai.util.TikTokensUtil;
import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.utils.RedisUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@ApiServiceMapping(title = "提示词")
public class PromptService {

    @Autowired
    private PromptMapper promptMapper;

    @Autowired
    private RedisUtils redisUtils;

    @PostConstruct
    public synchronized void initLocalCache() {
        List<PromptPO> promptPOList = promptMapper.selectList();
        redisUtils.set("prompt_ai", promptPOList);
    }

    @ApiMapping(value = "publicPrompt", title = "获取公开的提示词", description = "获取公开的提示词", returnType = false)
    public List<PromptPO> publicPrompt() {
        Object promptPOList = redisUtils.get("prompt_ai");
        return (List<PromptPO>) promptPOList;
    }
}

