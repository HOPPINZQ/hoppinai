package com.hoppinzq.file;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import org.springframework.stereotype.Service;

@Service
public class OSSServiceImpl implements FileService {

    private final static String accessKeyId = "";
    private final static String accessKeySecret = "";
    private final static String endpoint = "";

    private static OSS getClient() {
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
        return new OSSClientBuilder().build(endpoint, credentialsProvider);
    }

    @Override
    public void createBucket(String bucketName) {
        OSS ossClient = getClient();
        try {
            ossClient.createBucket(bucketName);
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
    }
}

