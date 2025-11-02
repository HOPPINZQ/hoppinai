package com.hoppinzq.red95.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.red95.api.Red95Api;
import com.hoppinzq.red95.model.*;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Red95Service {

    public static final String BASE_URL = "http://127.0.0.1:9003/";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final ObjectMapper mapper = defaultObjectMapper();

    private final Red95Api api;
    private final ExecutorService executorService;

    public Red95Service(final String token) {
        this(token, DEFAULT_TIMEOUT);
    }

    public Red95Service(final String token, final Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper);

        this.api = retrofit.create(Red95Api.class);
        this.executorService = client.dispatcher().executorService();
    }

    /**
     * 创建openai服务对象
     *
     * @param token    token
     * @param timeout  超时时间
     * @param proxyUrl 代理的url
     */
    public Red95Service(final String token, final Duration timeout, final String proxyUrl) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper, proxyUrl);

        this.api = retrofit.create(Red95Api.class);
        this.executorService = client.dispatcher().executorService();
    }


    /**
     * 创建openai服务对象
     *
     * @param token    token
     * @param proxyUrl 代理的url
     */
    public Red95Service(final String token, final String proxyUrl) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, DEFAULT_TIMEOUT);
        Retrofit retrofit = defaultRetrofit(client, mapper, proxyUrl);

        this.api = retrofit.create(Red95Api.class);
        this.executorService = client.dispatcher().executorService();
    }

    public Red95Service(final Red95Api api) {
        this.api = api;
        this.executorService = null;
    }

    public Red95Service(final Red95Api api, final ExecutorService executorService) {
        this.api = api;
        this.executorService = executorService;
    }

    /**
     * 执行一个Single类型的API调用。
     *
     * @param apiCall 要执行的API调用，类型为Single<T>。
     * @param <T>     API调用的返回类型。
     * @return 返回API调用的结果。
     * @throws Red95HttpException 如果API调用过程中发生HTTP异常，并且可以解析为OpenAiError时，将抛出此异常。
     */
    public static <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            try {
                if (e.response() == null || e.response().errorBody() == null) {
                    throw e;
                }
                String errorBody = e.response().errorBody().string();

                Red95Error error = mapper.readValue(errorBody, Red95Error.class);
                throw new Red95HttpException(error, e, e.code());
            } catch (IOException ex) {
                throw e;
            }
        }
    }

    public static Red95Api buildApi(String token, Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper);

        return retrofit.create(Red95Api.class);
    }

    public static ObjectMapper defaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        return mapper;
    }

    public static OkHttpClient defaultClient(String token, Duration timeout) {
        return new OkHttpClient.Builder()
                .addInterceptor(new AuthenticationInterceptor(token))
                .connectionPool(new ConnectionPool(5, 1, TimeUnit.SECONDS))
                .readTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .build();
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper) {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public static Retrofit defaultRetrofit(OkHttpClient client, ObjectMapper mapper, String proxyUrl) {
        return new Retrofit.Builder()
                .baseUrl(proxyUrl)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private static JsonNode parseJson(String nonStandardJson) {
        nonStandardJson = nonStandardJson.substring(nonStandardJson.indexOf("\"") + 1, nonStandardJson.lastIndexOf("\""));
        Pattern pattern = Pattern.compile("(\\w+):([^,}]+)");
        Matcher matcher = pattern.matcher(nonStandardJson);
        ObjectNode jsonNode = mapper.createObjectNode();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = matcher.group(2);
            jsonNode.put(key, value);
        }
        return jsonNode;
    }

    public static Flowable<SSE> stream(Call<ResponseBody> apiCall) {
        return stream(apiCall, false);
    }

    public static Flowable<SSE> stream(Call<ResponseBody> apiCall, boolean emitDone) {
        return Flowable.create(emitter -> apiCall.enqueue(new ResponseBodyCallback(emitter, emitDone)), BackpressureStrategy.BUFFER);
    }

    public static <T> Flowable<T> stream(Call<ResponseBody> apiCall, Class<T> cl) {
        return stream(apiCall).map(sse -> mapper.readValue(sse.getData(), cl));
    }

    public void shutdownExecutor() {
        Objects.requireNonNull(this.executorService, "executorService为空");
        this.executorService.shutdown();
    }

    public AgentCard getAgentCard() {
        return execute(api.agentCard());
    }

    public JSONRPCResponse task(JSONRPCRequest request) {
        return execute(api.task(request));
    }

    public Flowable<JSONRPCResponse> taskStream(JSONRPCRequest request) {
        return stream(api.taskStream(request), JSONRPCResponse.class);
    }
}
