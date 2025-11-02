package com.hoppinzq.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class OssConfig {

    /**
     * 将<YOUR-ENDPOINT>替换为OSS Endpoint，例如oss-cn-hangzhou.aliyuncs.com。
     */
    private static final String endpoint = "https://oss-cn-hangzhou.aliyuncs.com";

    /**
     * 通过环境变量 ALIBABA_CLOUD_ACCESS_KEY_ID 设置 accessKeyId
     */
    @Value("${oss.accessKeyId}")
    private String accessKeyId;

    /**
     * 通过环境变量 ALIBABA_CLOUD_ACCESS_KEY_Secret 设置 accessKeySecret
     */
    @Value("${oss.accessKeySecret}")
    private String accessKeySecret;

    private OSS ossClient;


    @Bean
    public OSS getSssClient() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

    @PreDestroy
    public void onDestroy() {
        ossClient.shutdown();
    }
}