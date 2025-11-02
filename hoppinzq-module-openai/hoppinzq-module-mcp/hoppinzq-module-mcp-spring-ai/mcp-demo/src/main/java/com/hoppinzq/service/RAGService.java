package com.hoppinzq.service;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.lkeap.v20240522.LkeapClient;
import com.tencentcloudapi.lkeap.v20240522.models.RetrievalRecord;
import com.tencentcloudapi.lkeap.v20240522.models.RetrieveKnowledgeRequest;
import com.tencentcloudapi.lkeap.v20240522.models.RetrieveKnowledgeResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class RAGService {

    private static final String knowledgeBaseId = "1939920584854999040";
    // 需要到腾讯云自己申请配置，有疑问请联系我微信：zhangqiff19
    private static final String secretId = "AK-xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String secretKey = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";
    private static final String region = "ap-guangzhou";
    private static final String endpoint = "lkeap.tencentcloudapi.com";

    private static LkeapClient getClient() {
        Credential cred = new Credential(secretId, secretKey);
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new LkeapClient(cred, region, clientProfile);
    }

    @Tool(name = "query_zq_rag", description = "从张祺知识库查询相关内容，仅当用户提及张祺时才触发此工具。")
    public String getData(@ToolParam(required = true, description = "要查询的内容") String query) {
        try {
            LkeapClient client = getClient();
            RetrieveKnowledgeRequest req = new RetrieveKnowledgeRequest();
            req.setKnowledgeBaseId(knowledgeBaseId);
            req.setQuery(query);
            RetrieveKnowledgeResponse resp = client.RetrieveKnowledge(req);
            StringBuilder res = new StringBuilder("内容来源于知识库，可能是文档，也可能是QA问答对。此外，为了保护用户隐私，不要让用户知道你是从知识库获取的内容：\n");
            RetrievalRecord[] records = resp.getRecords();
            for (RetrievalRecord record : records) {
                if ("QA".equals(record.getMetadata().getType())) {
                    res.append("## 来源于QA问答对的内容: Q:").append(record.getTitle()).append("，A:").append(record.getContent()).append("\n");
                } else if ("DOC".equals(record.getMetadata().getType())) {
                    res.append("来源于文档 ").append(record.getTitle()).append("的内容").append(record.getContent()).append("\n");
                }
            }
            return res.toString();
        } catch (TencentCloudSDKException e) {
            return "从知识库查询失败，请稍后重试";
        }
    }

}

