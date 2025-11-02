package com.hoppinzq.service.http;

import com.hoppinzq.service.util.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @author:ZhangQi 模拟http请求，封装get，post请求
 */
public class HoppinHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(HoppinHttpClient.class);
    private static final String COOKIE = "COOKIE";
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String APPLICATION_JSON = "application/json";
    private long costTime;
    private Map<String, String> requestHeader;
    private Map<String, List<String>> responseHeader;
    private String url;
    private String method;
    private String params;
    private String encode;
    private String charsetName;
    private String result;
    private String cookie;
    private int connectTimeout = 5000;
    private int readTimeout = 5000;
    private int code;

    public HoppinHttpClient() {
    }

    public HoppinHttpClient(Map<String, String> requestHeader, String url, String method, String params, String cookie, String encode, String charsetName, int connectTimeout, int readTimeout) {
        this.requestHeader = requestHeader;
        this.url = url;
        this.method = method;
        this.params = params;
        this.cookie = cookie;
        this.encode = encode;
        this.charsetName = charsetName;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    public static BuilderClient newBuilder() {
        return new BuilderClient();
    }

    public boolean isSuccess() {
        return 200 <= code && code <= 304;
    }

    public String sendRequest() {
        return sendRequest(this.getUrl(), this.getMethod(), null, null, false);
    }

    public String sendRequest(String url, String method) {
        return sendRequest(url, method, null, null, false);
    }

    public String sendRequest(String url, String method, Map<String, String> requestHeader) {
        return sendRequest(url, method, requestHeader, null, false);
    }

    public String sendRequest(String url, String method, Map<String, String> requestHeader, String params) {
        return sendRequest(url, method, requestHeader, params, false);
    }

    public String sendRequest(String url, String method, Map<String, String> requestHeader, String params, Boolean isBody) {
        HttpURLConnection connection = null;
        long start = System.currentTimeMillis();
        try {
            logger.info("请求的url是：" + url);
//            url= URLEncoder.encode(url.toString(),"UTF-8");
//            URI uri = URI.create(url);
//            URL targetUrl = uri.toURL();
//            this.setUrl(url);
            this.setUrl(url);
            URL targetUrl = new URL(url);
            connection = (HttpURLConnection) targetUrl.openConnection();
            connection.setConnectTimeout(this.getConnectTimeout());
            connection.setReadTimeout(this.getReadTimeout());
            if (requestHeader != null) {
                for (String header : requestHeader.keySet()) {
                    if (COOKIE.toLowerCase(Locale.ROOT).equals(header.toLowerCase(Locale.ROOT))) {
                        this.setCookie(requestHeader.get(header));
                    }
                    connection.setRequestProperty(header, requestHeader.get(header));
                }
            }
            this.setRequestHeader(requestHeader);
            if (method == null) {
                method = GET;
            } else {
                method = method.toUpperCase();
            }
            this.setMethod(method);
            connection.setRequestMethod(method);
            if (!GET.equals(method)) {
                connection.setRequestProperty("Content-Type", APPLICATION_JSON);
                connection.setDoOutput(true);
                OutputStream outputStream = connection.getOutputStream();
                if (params != null) {
                    outputStream.write(params.getBytes());
                }
                outputStream.flush();
                outputStream.close();
            }
            Map<String, List<String>> responseHeader = connection.getHeaderFields();
            this.setResponseHeader(responseHeader);
            int responseCode = connection.getResponseCode();
            this.setCode(responseCode);
            if (responseCode == HttpURLConnection.HTTP_OK) {
                logger.info("请求成功！");
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                String _response = response.toString();
                String _result = null;
                if (this.getEncode() == null && this.getCharsetName() == null) {
                    _result = _response;
                } else if (this.getCharsetName() == null) {
                    _result = new String(_response.getBytes(this.getEncode()));
                } else {
                    _result = new String(_response.getBytes(this.getEncode()), this.getCharsetName());
                }
                this.setResult(_result);
                if (!isBody) {
                    return _result;
                } else {
                    String formatJson = JSONUtil.format(_result);
                    return formatJson;
                }
            } else {
                logger.error("请求失败！");
                throw new RuntimeException("连接错误");
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            long end = System.currentTimeMillis();
            this.setCostTime(end - start);
        }
        return null;
    }

    public Map<String, String> getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(Map<String, String> requestHeader) {
        this.requestHeader = requestHeader;
    }

    public Map<String, List<String>> getResponseHeader() {
        return responseHeader;
    }

    public void setResponseHeader(Map<String, List<String>> responseHeader) {
        this.responseHeader = responseHeader;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public void parseCharset(String encode1, String encode2) {
        this.encode = encode1;
        this.charsetName = encode2;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }

    public long getCostTime() {
        return costTime;
    }

    public void setCostTime(long costTime) {
        this.costTime = costTime;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public static class BuilderClient {
        private Map<String, String> header;
        private String url;
        private String method;
        private String params;
        private String encode;
        private String charset;
        private String cookie;
        private int connectTimeout = 5000;
        private int readTimeout = 5000;

        public BuilderClient header(Map<String, String> requestHeader) {
            this.header = requestHeader;
            return this;
        }

        public BuilderClient url(String url) {
            this.url = url;
            return this;
        }

        public BuilderClient method(String method) {
            this.method = method;
            return this;
        }

        public BuilderClient params(String params) {
            this.params = params;
            return this;
        }

        public BuilderClient encode(String encode) {
            this.encode = encode;
            return this;
        }

        public BuilderClient charset(String charset) {
            this.charset = charset;
            return this;
        }

        public BuilderClient cookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public BuilderClient connectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public BuilderClient readTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public HoppinHttpClient build() {
            return new HoppinHttpClient(header, url, method, params, cookie, encode, charset, connectTimeout, readTimeout);
        }

        public String sendRequest() throws IOException {
            return build().sendRequest();
        }
    }
}