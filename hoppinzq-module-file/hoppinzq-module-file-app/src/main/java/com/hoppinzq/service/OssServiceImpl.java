package com.hoppinzq.service;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.common.auth.CredentialsProvider;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.hoppinzq.api.OssService;
import com.hoppinzq.service.annotation.ServiceRegister;
import com.hoppinzq.service.annotation.ServiceRegisterMethod;
import com.hoppinzq.service.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Value;

import java.io.InputStream;

@ServiceRegister
public class OssServiceImpl implements OssService {

    @Value("${oss.endpoint}")
    private String endpoint;

    @Value("${oss.accessKeyId}")
    private String accessKeyId;
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    @ServiceRegisterMethod(title = "上传微博头像", description = "上传微博头像")
    public String uploadFile(InputStream inputStream) {
        String bucketName = "hoppinzq";
        String dirName = "weibo";
        String uuid = UUIDUtil.getUUID();
        String fileName = uuid + ".jpg";
        String objectName = dirName + "/" + fileName;
        CredentialsProvider credentialsProvider = new DefaultCredentialProvider(accessKeyId, accessKeySecret);
        OSS ossClient = new OSSClientBuilder().build(endpoint, credentialsProvider);
        try {
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (OSSException oe) {
            oe.printStackTrace();
        } catch (ClientException ce) {
            ce.printStackTrace();
        } finally {
            if (ossClient != null) {
                ossClient.shutdown();
            }
        }
        return "https://hoppinzq.oss-cn-hangzhou.aliyuncs.com/weibo/" + fileName;
    }
}

