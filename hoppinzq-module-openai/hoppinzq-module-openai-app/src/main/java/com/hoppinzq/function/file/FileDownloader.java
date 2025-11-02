package com.hoppinzq.function.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloader {
    public static void downloadFile(String fileUrl, String saveDir) throws IOException {
        URL url = new URL(fileUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setConnectTimeout(5000); // 5秒连接超时
        connection.setReadTimeout(15000);   // 15秒读取超时
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("服务器返回非200状态码: " + responseCode);
        }
        // 获取文件名
        String fileName = getFileNameFromUrl(url, connection);
        // 创建保存目录（如果不存在）
        File directory = new File(saveDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        try (InputStream in = new BufferedInputStream(connection.getInputStream());
             FileOutputStream out = new FileOutputStream(saveDir + File.separator + fileName)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            long fileSize = connection.getContentLength(); // 文件大小（可能不可用）
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
                totalBytesRead += bytesRead;
                // 如果知道文件大小，可以显示进度
                if (fileSize > 0) {
                    double progress = (totalBytesRead * 100.0) / fileSize;
                    System.out.printf("下载进度: %.2f%%%n", progress);
                }
            }
            System.out.println("文件下载完成：" + fileName);
        }
    }

    private static String getFileNameFromUrl(URL url, HttpURLConnection connection) {
        // 1. 尝试从Content-Disposition头获取文件名
        String disposition = connection.getHeaderField("Content-Disposition");
        if (disposition != null) {
            int index = disposition.indexOf("filename=");
            if (index > 0) {
                String fileName = disposition.substring(index + 9).replace("\"", "");
                if (!fileName.isEmpty()) return fileName;
            }
        }
        // 2. 从URL路径提取文件名
        String path = url.getPath();
        int lastSlashIndex = path.lastIndexOf('/');
        if (lastSlashIndex >= 0 && lastSlashIndex < path.length() - 1) {
            return path.substring(lastSlashIndex + 1);
        }
        // 3. 使用默认文件名
        return "downloaded_file";
    }

    public static void main(String[] args) {
        try {
            downloadFile(
                    "https://openai.apifox.cn/raiz5jee8eiph0eeFooV/api/v1/projects/2100343/published-projects/export-data?branchId=2507834",
                    "D:\\ai"
            );
        } catch (IOException e) {
            System.err.println("下载失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
