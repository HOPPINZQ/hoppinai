package com.hoppinzq.function.zq.wyy;


import cn.hutool.crypto.digest.MD5;
import okhttp3.*;

import java.io.IOException;

public class WyyFunctionCall {

    public static void main(String[] args) throws IOException {
        String s = MD5.create().digestHex16("19ZHANGqi");
        System.err.println(s);
        System.err.println(System.currentTimeMillis());

        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("text/plain");
        RequestBody body = RequestBody.create(mediaType, "");
        Request request = new Request.Builder()
                .url("http://hoppin.cn:3000/vip/tasks")
                .method("GET", null)
                .build();
        Response response = client.newCall(request).execute();
        System.err.println(response.body().string());
    }

}

