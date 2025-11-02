package com.hoppinzq.service.util.http;

import okhttp3.*;

import java.io.File;
import java.io.IOException;
import java.util.Map;


public class HoppinzqHttpUtils {

    private HoppinzqHttpUtils() {
    }

    public static Response get(String url) throws IOException {
        return get(url, null);
    }

    public static String getString(String url) throws IOException {
        Response response = get(url, null);
        return set(response);
    }

    public static Response get(String url, Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request.Builder builder = new Request.Builder().url(url)
                .method("GET", null);
        if (headers != null) {
            for (String key : headers.keySet()) {
                builder.header(key, headers.get(key));
            }
        }
        return client.newCall(builder.build()).execute();
    }

    public static String getString(String url, Map<String, String> headers) throws IOException {
        Response response = get(url, headers);
        return set(response);
    }

    public static Response post(String url) throws IOException {
        return post(url, "", null);
    }

    public static String postString(String url) throws IOException {
        Response response = post(url, "", null);
        return set(response);
    }

    public static Response post(String url, String json) throws IOException {
        return post(url, json, null);
    }

    public static String postString(String url, String json) throws IOException {
        Response response = post(url, json, null);
        return set(response);
    }

    public static Response post(String url, File file) throws IOException {
        return post(url, file, null);
    }

    public static String postString(String url, File file) throws IOException {
        Response response = post(url, file, null);
        return set(response);
    }

    public static Response post(String url, Map<String, String> headers) throws IOException {
        return post(url, "", headers);
    }

    public static String postString(String url, Map<String, String> headers) throws IOException {
        Response response = post(url, "", headers);
        return set(response);
    }

    public static Response post(String url, String json, Map<String, String> headers) throws IOException {
        return post(url, json, null, headers);
    }

    public static String postString(String url, String json, Map<String, String> headers) throws IOException {
        Response response = post(url, json, null, headers);
        return set(response);
    }

    public static Response post(String url, File file, Map<String, String> headers) throws IOException {
        return post(url, null, file, headers);
    }

    public static String postString(String url, File file, Map<String, String> headers) throws IOException {
        Response response = post(url, null, file, headers);
        return set(response);
    }

    public static Response post(String url, String json, File file, Map<String, String> headers) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body;
        Request.Builder builder = new Request.Builder();
        if (file != null) {
            body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                    .addFormDataPart("file", file.getPath(),
                            RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
        } else {
            body = RequestBody.create(mediaType, json);
            builder.addHeader("Content-Type", "application/json");
        }
        builder.url(url)
                .method("POST", body);
        if (headers != null) {
            for (String key : headers.keySet()) {
                builder.header(key, headers.get(key));
            }
        }
        return client.newCall(builder.build()).execute();
    }

    public static String postString(String url, String json, File file, Map<String, String> headers) throws IOException {
        Response response = post(url, json, file, headers);
        return set(response);
    }

    private static String set(Response response) throws IOException {
        return response.body().string();
    }
}

