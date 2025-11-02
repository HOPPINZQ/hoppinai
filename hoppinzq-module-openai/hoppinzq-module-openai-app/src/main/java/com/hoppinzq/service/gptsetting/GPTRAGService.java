package com.hoppinzq.service.gptsetting;

import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.tencentcloudapi.common.AbstractModel;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.lkeap.v20240522.LkeapClient;
import com.tencentcloudapi.lkeap.v20240522.models.ListDocsRequest;
import com.tencentcloudapi.lkeap.v20240522.models.ListDocsResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApiServiceMapping(title = "gpt对话", roleType = ApiServiceMapping.RoleType.RIGHT)
public class GPTRAGService {

    private static final String knowledgeBaseId = "";
    private static final String secretId = "";
    private static final String secretKey = "";

    // 新增
    public static void main(String[] args) {
        queryFile();
    }

    public static void queryFile() {
        try {
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("lkeap.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            LkeapClient client = new LkeapClient(cred, "ap-guangzhou", clientProfile);
            ListDocsRequest req = new ListDocsRequest();
            req.setKnowledgeBaseId(knowledgeBaseId);
            ListDocsResponse resp = client.ListDocs(req);
            System.out.println(AbstractModel.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
        }
    }

}

