package com.hoppinzq.function.file;

import okhttp3.*;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class FileApiFoxDownload {

    public static void main(String[] args) throws IOException {

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json;charset=UTF-8");
        RequestBody body = RequestBody.create(mediaType, "{\r\n    \"type\": \"html\",\r\n    \"id\": \"5435161\",\r\n    \"version\": \"openapi30\",\r\n    \"branchId\": 5498629,\r\n    \"projectId\": 5813541\r\n}");
        Request request = new Request.Builder()
                .url("https://openai.apifox.cn/raiz5jee8eiph0eeFooV/api/v1/projects/5435161/published-projects/export-data?branchId=5498629")
                .method("POST", body)
                .addHeader("authority", "openai.apifox.cn")
                .addHeader("access-control-allow-credentials", "true")
                .addHeader("x-client-version", "2.2.30")
                .addHeader("x-device-id", "7eq6HW5D-T1pU-pb7E-qGf1-EVTw1LzgZ8Mv")
                .addHeader("x-project-id", "5435161")
                .addHeader("Cookie", "publishedProject=kHiovquj0BG2WycRdDshsQ; _ga=GA1.1.1676098216.1741593205; _gcl_au=1.1.797277723.1741593206; acw_tc=aa404c5b68ad87f13c66fb934e654b9da2788c39f53251b653906835b6dd2936; Hm_lvt_56f20ef8b9cc36436162ef11afabac21=1741593204,1742266365,1742447734; Hm_lpvt_56f20ef8b9cc36436162ef11afabac21=1742447734; HMACCOUNT=DF8BD8B4CB0842E1; _ga_NTLDK3J296=GS1.1.1742447734.3.1.1742448512.60.0.0")
                .addHeader("X-ZQ-Ignore", "1")
                .addHeader("User-Agent", "Apifox/1.0.0 (https://apifox.com)")
                .addHeader("content-type", "application/json;charset=UTF-8")
                .build();
        Response response = client.newCall(request).execute();
        String content = response.body().string();
        String filePath = "D:\\ai\\ai1.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(content);
            writer.newLine();  // 写入换行符
        } catch (IOException e) {
            System.err.println("写入文件时出错: " + e.getMessage());
        }
    }
}

