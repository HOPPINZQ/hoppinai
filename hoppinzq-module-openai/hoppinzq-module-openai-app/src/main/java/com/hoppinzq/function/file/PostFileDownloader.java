package com.hoppinzq.function.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PostFileDownloader {
    public static void downloadFileWithPost(String fileUrl,
                                            String postData,
                                            String saveDir) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        // 设置POST请求参数
        connection.setRequestMethod("POST");
        connection.setDoOutput(true); // 允许输出请求体
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(15000);
        // 设置请求头（根据实际情况调整）
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setRequestProperty("Accept", "application/octet-stream");
        // 发送POST数据
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = postData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("服务器返回非200状态码: " + responseCode);
        }
        // 获取文件名
        String fileName = getFileNameFromResponse(connection);
        // 创建保存目录
        File directory = new File(saveDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        // 下载文件
        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream out = new FileOutputStream(saveDir + File.separator + fileName)) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            long totalBytes = 0;
            long contentLength = connection.getContentLengthLong();
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytes += bytesRead;
                // 显示下载进度
                if (contentLength > 0) {
                    double progress = (totalBytes * 100.0) / contentLength;
                    System.out.printf("\r下载进度: %.2f%%", progress);
                }
            }
            System.out.println("\n下载完成：" + fileName);
        }
    }

    private static String getFileNameFromResponse(HttpURLConnection connection) {
        // 1. 从Content-Disposition头获取
        String disposition = connection.getHeaderField("Content-Disposition");
        if (disposition != null) {
            String[] tokens = disposition.split(";");
            for (String token : tokens) {
                if (token.trim().startsWith("filename=")) {
                    return token.substring(token.indexOf('=') + 1)
                            .replace("\"", "")
                            .replace("?", "_")
                            .trim();
                }
            }
        }
        // 2. 从URL路径获取
        String path = connection.getURL().getPath();
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash != -1) {
            return path.substring(lastSlash + 1);
        }
        // 3. 默认文件名
        return "downloaded_file_" + System.currentTimeMillis();
    }

    public static void main(String[] args) {
        try {
            // 示例1：表单参数
//            String formData = "username=test&fileId=123";
//            downloadFileWithPost(
//                    "https://example.com/download",
//                    formData,
//                    "downloads"
//            );
            // 示例2：JSON参数

            String jsonData = "{\"type\": \"markdown\",\"id\": \"5813541\",\"version\": \"openapi30\",\"branchId\": 2507834,\"projectId\": 5813541}";
            downloadFileWithPost(
                    "https://openai.apifox.cn/raiz5jee8eiph0eeFooV/api/v1/projects/2100343/published-projects/export-data?branchId=2507834",
                    jsonData,
                    "D:\\ai"
            );

        } catch (IOException e) {
            System.err.println("下载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
