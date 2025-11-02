package com.hoppinzq.service.cdn;

import com.hoppinzq.service.annotation.ApiMapping;
import com.hoppinzq.service.annotation.ApiServiceMapping;
import com.hoppinzq.service.bean.ApiResponse;
import com.tencentcloudapi.cdn.v20180606.CdnClient;
import com.tencentcloudapi.cdn.v20180606.models.*;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

@ApiServiceMapping(title = "cdn服务", description = "cdn服务", roleType = ApiServiceMapping.RoleType.RIGHT)
public class CDNService {

    private static final String secretId = "";
    private static final String secretKey = "";
    private static final String region = "ap-guangzhou";
    private static final String endpoint = "cdn.tencentcloudapi.com";

    @ApiMapping(value = "PurgeUrlsCache", title = "刷新url", description = "刷新url", returnType = false)
    public ApiResponse reUrl(String url) {
        try {
            Credential cred = new Credential("", "");
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            CdnClient client = new CdnClient(cred, "", clientProfile);
            PurgeUrlsCacheRequest req = new PurgeUrlsCacheRequest();
            String[] urls1 = {url};
            req.setUrls(urls1);
            PurgeUrlsCacheResponse resp = client.PurgeUrlsCache(req);
            String jsonString = PurgeUrlsCacheResponse.toJsonString(resp);
            return ApiResponse.success(jsonString);
        } catch (TencentCloudSDKException e) {
            return ApiResponse.fail(5511, e.getMessage());
        }
    }

    @ApiMapping(value = "PurgePathCache", title = "刷新目录", description = "刷新目录", returnType = false)
    public ApiResponse rePath(String url) {
        try {
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            CdnClient client = new CdnClient(cred, "", clientProfile);
            PurgePathCacheRequest req = new PurgePathCacheRequest();
            String[] paths1 = {"https://hoppinzq.com"};
            req.setPaths(paths1);
            PurgePathCacheResponse resp = client.PurgePathCache(req);
            String jsonString = PurgePathCacheResponse.toJsonString(resp);
            return ApiResponse.success(jsonString);
        } catch (TencentCloudSDKException e) {
            return ApiResponse.fail(5511, e.getMessage());
        }
    }

    @ApiMapping(value = "cdnquota", title = "刷新的配额", description = "刷新的配额", returnType = false)
    public String quota() {
        try {
            Credential cred = new Credential(secretId, secretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint(endpoint);
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            CdnClient client = new CdnClient(cred, "", clientProfile);
            DescribePurgeQuotaRequest req = new DescribePurgeQuotaRequest();
            DescribePurgeQuotaResponse resp = client.DescribePurgeQuota(req);
            return DescribePurgeQuotaResponse.toJsonString(resp);
        } catch (TencentCloudSDKException e) {
            return e.getMessage();
        }
    }

}
