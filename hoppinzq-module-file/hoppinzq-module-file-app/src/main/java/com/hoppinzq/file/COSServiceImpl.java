package com.hoppinzq.file;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.CannedAccessControlList;
import com.qcloud.cos.model.CreateBucketRequest;
import com.qcloud.cos.region.Region;
import org.springframework.stereotype.Service;

@Service
public class COSServiceImpl implements FileService {

    private static String defaultBucketName = "";
    private static String address = "";
    private static String accessKeyId = "";
    private static String accessKeySecret = "";
    private static String region = "ap-guangzhou";

    private static COSClient getClient() {
        COSCredentials cred = new BasicCOSCredentials(accessKeyId, accessKeySecret);
        ClientConfig clientConfig = new ClientConfig(new Region(region));
        COSClient cosClient = new COSClient(cred, clientConfig);
        return cosClient;
    }

    @Override
    public void createBucket(String bucketName) {
        try {
            CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
            createBucketRequest.setCannedAcl(CannedAccessControlList.Private);
            getClient().createBucket(createBucketRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

