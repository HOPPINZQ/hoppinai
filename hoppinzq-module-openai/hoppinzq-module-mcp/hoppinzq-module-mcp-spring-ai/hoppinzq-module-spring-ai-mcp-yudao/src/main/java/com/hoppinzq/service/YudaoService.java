package com.hoppinzq.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hoppinzq.api.YudaoApi;
import com.hoppinzq.bean.*;
import com.hoppinzq.model.CommonResult;
import com.hoppinzq.model.YudaoError;
import com.hoppinzq.model.YudaoHttpException;
import io.reactivex.Single;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.HttpException;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YudaoService {

    public static final String BASE_URL = "http://localhost:48080/admin-api/";
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);
    private static final ObjectMapper mapper = defaultObjectMapper();

    private final YudaoApi api;
    private final ExecutorService executorService;

    public YudaoService(final String token) {
        this(token, DEFAULT_TIMEOUT);
    }

    public YudaoService(final String token, final Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper);

        this.api = retrofit.create(YudaoApi.class);
        this.executorService = client.dispatcher().executorService();
    }

    public static <T> T execute(Single<T> apiCall) {
        try {
            return apiCall.blockingGet();
        } catch (HttpException e) {
            try {
                if (e.response() == null || e.response().errorBody() == null) {
                    throw e;
                }
                String errorBody = e.response().errorBody().string();

                YudaoError error = mapper.readValue(errorBody, YudaoError.class);
                throw new YudaoHttpException(error, e, e.code());
            } catch (IOException ex) {
                throw e;
            }
        }
    }

    public static YudaoApi buildApi(String token, Duration timeout) {
        ObjectMapper mapper = defaultObjectMapper();
        OkHttpClient client = defaultClient(token, timeout);
        Retrofit retrofit = defaultRetrofit(client, mapper);

        return retrofit.create(YudaoApi.class);
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

    public void shutdownExecutor() {
        Objects.requireNonNull(this.executorService, "executorService为空");
        this.executorService.shutdown();
    }

    public CommonResult<List<TenantRespVO>> getTenantSimpleList() {
        return execute(api.tenantSimpleList());
    }

    public CommonResult<UserRespVO> getUserById(Long id) {
        return execute(api.getUserById(id));
    }

    public CommonResult<PageResult<UserRespVO>> getUserPageByUserName(String userName) {
        return execute(api.getUserPageByUserName(userName, 1, 10));
    }

    public CommonResult<List<DeptSimpleRespVO>> getSimpleDeptList() {
        return execute(api.getSimpleDeptList());
    }

    public CommonResult<Long> createUser(UserSaveReqVO userSaveReqVO) {
        return execute(api.createUser(userSaveReqVO));
    }

    public CommonResult<Boolean> assignUserDept(Long userId, Long deptId) {
        return execute(api.assignUserDept(userId, deptId));
    }

    public CommonResult<List<RoleRespVO>> getRoleSimpleList() {
        return execute(api.roleSimpleList());
    }

    public CommonResult<Boolean> assignUserRole(PermissionAssignUserRoleReqVO reqVO) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("userId", reqVO.getUserId());
        objectNode.putPOJO("roleIds", reqVO.getRoleIds());
        return execute(api.assignUserRole(objectNode));
    }

    public CommonResult<ObjectNode> loginById(Long userId) {
        return execute(api.loginById(userId));
    }
}
