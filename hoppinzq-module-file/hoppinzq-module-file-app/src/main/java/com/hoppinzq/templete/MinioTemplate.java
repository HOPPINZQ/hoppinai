package com.hoppinzq.templete;

import com.hoppinzq.context.FileVo;
import com.hoppinzq.util.UUIDUtil;
import io.minio.*;
import io.minio.http.Method;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class MinioTemplate {

    @Autowired
    private MinioClient client;

    /**
     * 上传文件
     *
     * @param file
     * @param bucketName
     * @return
     */
    public FileVo upload(MultipartFile file, String bucketName) {
        try {
            createBucket(bucketName);

            String oldName = file.getOriginalFilename();
            String fileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + UUIDUtil.getUUID() + oldName.substring(oldName.lastIndexOf("."));

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), 0)
                            .contentType(file.getContentType()).build()
            );

            String url = this.getObjUrl(bucketName, fileName);
            return FileVo.builder()
                    .oldFileName(oldName)
                    .newFileName(fileName)
                    .fileUrl(url.substring(0, url.indexOf("?")))
                    .build();
        } catch (Exception e) {
            log.error("上传文件出错:{}", e);
            return null;
        }
    }

    /**
     * 上传多个文件
     *
     * @param files
     * @param bucketName
     * @return
     */
    public List<FileVo> uploads(List<MultipartFile> files, String bucketName) {
        try {
            List<FileVo> list = new ArrayList<>();
            createBucket(bucketName);

            for (MultipartFile file : files) {
                String oldName = file.getOriginalFilename();
                String fileName = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + UUIDUtil.getUUID() + oldName.substring(oldName.lastIndexOf("."));

                client.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(file.getInputStream(), file.getSize(), 0)
                                .contentType(file.getContentType()).build()
                );

                String url = this.getObjUrl(bucketName, fileName);
                list.add(
                        FileVo.builder()
                                .oldFileName(oldName)
                                .newFileName(fileName)
                                .fileUrl(url.substring(0, url.indexOf("?")))
                                .build()
                );
            }
            return list;
        } catch (Exception e) {
            log.error("上传文件出错:{}", e);
            return null;
        }
    }

    /**
     * 下载文件
     *
     * @param bucketName
     * @param fileName
     * @throws Exception
     */
    public void download(String bucketName, String fileName) throws Exception {
        client.downloadObject(DownloadObjectArgs.builder().bucket(bucketName).filename(fileName).build());
    }

    /**
     * 获取文件链接
     *
     * @param bucketName
     * @param fileName
     * @return
     * @throws Exception
     */
    public String getObjUrl(String bucketName, String fileName) throws Exception {
        return client.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .method(Method.GET)
                        .expiry(30, TimeUnit.SECONDS)
                        .build()
        );
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param fileName
     * @throws Exception
     */
    public void delete(String bucketName, String fileName) throws Exception {
        client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
    }

    @SneakyThrows
    public void createBucket(String bucketName) {
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build())) {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            String sb = "{\"Version\": \"2012-10-17\",\"Statement\": [{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:GetBucketLocation\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "\"]},{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:ListBucket\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "\"],\"Condition\": {\"StringEquals\": {\"s3:prefix\": [\"*\"]}}},{\"Effect\": \"Allow\",\"Principal\": {\"AWS\": [\"*\"]},\"Action\": [\"s3:GetObject\"],\"Resource\": [\"arn:aws:s3:::" + bucketName + "/**\"]}]}";
            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(sb).build());
        }
    }

}
