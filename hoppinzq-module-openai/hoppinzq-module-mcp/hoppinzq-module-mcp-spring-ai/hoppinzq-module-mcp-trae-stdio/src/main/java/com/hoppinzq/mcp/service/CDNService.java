package com.hoppinzq.mcp.service;

import com.hoppinzq.mcp.bean.MCPResponse;
import com.tencentcloudapi.cdn.v20180606.CdnClient;
import com.tencentcloudapi.cdn.v20180606.models.PurgePathCacheRequest;
import com.tencentcloudapi.cdn.v20180606.models.PurgePathCacheResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
public class CDNService {

    /**
     * 刷新指定URL的CDN缓存
     *
     * @param url 要刷新网页的链接，支持单个URL路径
     * @return MCPResponse 包含操作结果的响应对象：
     * - 成功时返回JSON格式的PurgePathCacheResponse
     * - 失败时返回错误信息
     */
    @Tool(name = "flush_cdn_cache", description = "刷新CDN缓存")
    public MCPResponse flushCache(@ToolParam(description = "要刷新网页的链接") String url) {
        try {
            CdnClient client = getClient();
            PurgePathCacheRequest req = new PurgePathCacheRequest();
            String[] paths = {"https://hoppinzq.com", url};
            req.setFlushType("flush");
            req.setPaths(paths);
            PurgePathCacheResponse resp = client.PurgePathCache(req);
            String jsonString = PurgePathCacheResponse.toJsonString(resp);
            return MCPResponse.success(jsonString);
        } catch (Exception e) {
            return MCPResponse.fail(e.getMessage());
        }
    }

    private CdnClient getClient() {
        String endpoint = "";
        Credential cred = new Credential("", "");
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint(endpoint);
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);
        return new CdnClient(cred, "", clientProfile);
    }

    public static void main(String[] args) {
        CDNService cdnService = new CDNService();
        MCPResponse response = cdnService.flushCache("https://hoppinzq.com");
        System.out.println(response);
    }
}
