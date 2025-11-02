package com.hoppinzq.base.controller;

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URL;
import java.util.Date;

/**
 * Applicationx
 *
 * @author peiyu
 * @date 2023/10/24
 */
@Controller
public class PresignedURLController {

    /**
     * 将<YOUR-BUCKET>替换为Bucket名称。
     * 指定上传到OSS的文件前缀。
     * 姜<YOUR-OBJECT>替换为Object完整路径，例如exampleobject.txt。Object完整路径中不能包含Bucket名称。
     * 指定过期时间，单位为毫秒。
     */
    private static final String BUCKET_NAME = "demo-zq";
    private static final String OBJECT_NAME = "demo/demo.png";
    private static final long EXPIRE_TIME = 3600 * 1000L;

    @Autowired
    private OSS ossClient;

    @GetMapping("/get_presigned_url_for_oss_upload")
    @ResponseBody
    public String generatePresignedURL() {
        try {
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(BUCKET_NAME, OBJECT_NAME, HttpMethod.PUT);
            Date expiration = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            request.setExpiration(expiration);
            request.setContentType("image/png");
            URL signedUrl = ossClient.generatePresignedUrl(request);
            return signedUrl.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}