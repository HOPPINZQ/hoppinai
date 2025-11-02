package com.hoppinzq.file;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MinioServiceImpl implements FileService {

    @Autowired
    private MinioClient client;

    @Override
    public void createBucket(String bucketName) {
        try {
            if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                String sb = "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:GetBucketLocation\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "\"]},{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:ListBucket\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "\"],\"Condition\": {\"StringEquals\": {\"s3:prefix\": [\"*\"]}}},{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:GetObject\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "/**\"]}]}";
                client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(sb).build());
            } else {
                System.out.println("Bucket已经存在：" + bucketName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

