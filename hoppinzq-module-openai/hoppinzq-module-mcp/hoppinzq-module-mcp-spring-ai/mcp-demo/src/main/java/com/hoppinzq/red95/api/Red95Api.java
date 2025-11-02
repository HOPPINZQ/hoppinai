package com.hoppinzq.red95.api;

import com.hoppinzq.red95.model.AgentCard;
import com.hoppinzq.red95.model.JSONRPCRequest;
import com.hoppinzq.red95.model.JSONRPCResponse;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * 红警95的 API 接口定义
 */
public interface Red95Api {

    @GET(".well-known/agent-card")
    Single<AgentCard> agentCard();

    @POST("a2a")
    Single<JSONRPCResponse> task(@Body JSONRPCRequest request);

    @Streaming
    @POST("/a2a/stream")
    Call<ResponseBody> taskStream(@Body JSONRPCRequest request);
}
