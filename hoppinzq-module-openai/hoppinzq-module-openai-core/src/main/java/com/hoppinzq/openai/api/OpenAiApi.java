package com.hoppinzq.openai.api;

import com.hoppinzq.model.openai.DeleteResult;
import com.hoppinzq.model.openai.OpenAiResponse;
import com.hoppinzq.model.openai.assistants.*;
import com.hoppinzq.model.openai.audio.CreateSpeechRequest;
import com.hoppinzq.model.openai.audio.TranscriptionResult;
import com.hoppinzq.model.openai.audio.TranslationResult;
import com.hoppinzq.model.openai.billing.BillingUsage;
import com.hoppinzq.model.openai.billing.Subscription;
import com.hoppinzq.model.openai.completion.CompletionRequest;
import com.hoppinzq.model.openai.completion.CompletionResult;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionRequest;
import com.hoppinzq.model.openai.completion.chat.ChatCompletionResult;
import com.hoppinzq.model.openai.edit.EditRequest;
import com.hoppinzq.model.openai.edit.EditResult;
import com.hoppinzq.model.openai.embedding.EmbeddingRequest;
import com.hoppinzq.model.openai.embedding.EmbeddingResult;
import com.hoppinzq.model.openai.engine.Engine;
import com.hoppinzq.model.openai.file.File;
import com.hoppinzq.model.openai.fine_tuning.FineTuningEvent;
import com.hoppinzq.model.openai.fine_tuning.FineTuningJob;
import com.hoppinzq.model.openai.fine_tuning.FineTuningJobRequest;
import com.hoppinzq.model.openai.finetune.FineTuneEvent;
import com.hoppinzq.model.openai.finetune.FineTuneRequest;
import com.hoppinzq.model.openai.finetune.FineTuneResult;
import com.hoppinzq.model.openai.image.CreateImageRequest;
import com.hoppinzq.model.openai.image.ImageResult;
import com.hoppinzq.model.openai.messages.Message;
import com.hoppinzq.model.openai.messages.MessageFile;
import com.hoppinzq.model.openai.messages.MessageRequest;
import com.hoppinzq.model.openai.messages.ModifyMessageRequest;
import com.hoppinzq.model.openai.model.Model;
import com.hoppinzq.model.openai.moderation.ModerationRequest;
import com.hoppinzq.model.openai.moderation.ModerationResult;
import com.hoppinzq.model.openai.runs.*;
import com.hoppinzq.model.openai.threads.Thread;
import com.hoppinzq.model.openai.threads.ThreadRequest;
import io.reactivex.Single;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * OpenAI API 接口定义
 */
public interface OpenAiApi {

    /**
     * 列出所有模型。
     * 此方法用于获取OpenAI平台上的所有可用模型信息。
     *
     * @return Single<OpenAiResponse < Model>>：返回一个Single对象，包含模型信息的响应。
     */
    @GET("v1/models")
    Single<OpenAiResponse<Model>> listModels();

    /**
     * 根据模型ID获取模型信息。
     *
     * @param modelId 模型的唯一标识符
     * @return 单个模型的响应，包含模型详细信息
     */
    @GET("/v1/models/{model_id}")
    Single<Model> getModel(@Path("model_id") String modelId);

    /**
     * 创建补全结果。
     * 此方法用于通过发送POST请求到"/v1/completions"路径，并提交一个补全请求，以获取补全结果。
     *
     * @param request 补全请求对象，包含创建补全所需的参数和数据。
     * @return Single<CompletionResult> 一个Single对象，它表示异步操作的返回值，其中包含补全结果。
     */
    @POST("/v1/completions")
    Single<CompletionResult> createCompletion(@Body CompletionRequest request);

    /**
     * 流式创建补全请求。
     * 此方法用于通过HTTP POST请求向"/v1/completions"端点发送补全请求，并以流式方式接收响应。
     *
     * @param request 补全请求对象，包含创建补全所需的参数和数据。
     * @return Call<ResponseBody> 返回一个Call对象，该对象可用于执行请求并接收响应。
     */
    @Streaming
    @POST("/v1/completions")
    Call<ResponseBody> createCompletionStream(@Body CompletionRequest request);

    /**
     * 创建聊天补全请求。
     * <p>
     * 此方法用于向指定的API端点发送聊天补全请求，并返回一个包含聊天补全结果的Single对象。
     *
     * @param request 聊天补全请求对象，包含请求所需的所有参数和数据。
     * @return Single<ChatCompletionResult> 一个Single对象，它将在请求成功时发射一个ChatCompletionResult对象，或者在请求失败时发射一个错误。
     */
    @POST("/v1/chat/completions")
    Single<ChatCompletionResult> createChatCompletion(@Body ChatCompletionRequest request);

    /**
     * 创建聊天补全的流式响应。
     * 此方法用于通过POST请求发送聊天补全请求，并返回一个流式响应体，适用于实时聊天补全场景。
     *
     * @param request 聊天补全请求对象，包含用户输入的文本和其他相关参数。
     * @return Call<ResponseBody> 返回一个Call对象，可用于执行异步HTTP请求并获取响应体。
     */
    @Streaming
    @POST("/v1/chat/completions")
    Call<ResponseBody> createChatCompletionStream(@Body ChatCompletionRequest request);

    /**
     * 已弃用。
     * 向指定引擎ID发送完成请求。
     * 此方法已被标记为过时，不推荐使用。
     *
     * @param engineId 引擎的ID，用于唯一标识目标引擎。
     * @param request  包含完成请求详细信息的CompletionRequest对象。
     * @return Single<CompletionResult> 返回一个Single对象，该对象在完成请求成功后发出CompletionResult。
     * @deprecated 请使用新的API接口代替此方法。
     */
    @Deprecated
    @POST("/v1/engines/{engine_id}/completions")
    Single<CompletionResult> createCompletion(@Path("engine_id") String engineId, @Body CompletionRequest request);

    /**
     * 创建编辑请求。
     * <p>
     * 此方法用于向服务器发送一个编辑请求，并通过POST方法将编辑请求体发送到"/v1/edits"路径。
     *
     * @param request 编辑请求体，包含了需要编辑的内容和相关信息。
     * @return Single<EditResult> 一个Single对象，它将返回一个EditResult对象，该对象包含了编辑请求的结果。
     */
    @POST("/v1/edits")
    Single<EditResult> createEdit(@Body EditRequest request);

    /**
     * 已弃用。创建编辑请求，向指定引擎ID发送编辑请求。
     *
     * @param engineId 引擎的唯一标识符。
     * @param request  编辑请求的具体内容。
     * @return Single<EditResult> 返回一个Single对象，该对象封装了编辑请求的结果。
     * @deprecated 使用此方法已被弃用，请使用新的API。
     */
    @Deprecated
    @POST("/v1/engines/{engine_id}/edits")
    Single<EditResult> createEdit(@Path("engine_id") String engineId, @Body EditRequest request);

    /**
     * 创建嵌入向量。
     * 此方法用于向服务器发送一个嵌入请求，并接收返回的嵌入结果。
     *
     * @param request 嵌入请求对象，包含创建嵌入所需的所有信息。
     * @return Single<EmbeddingResult> 一个Single对象，用于表示异步操作的结果。
     */
    @POST("/v1/embeddings")
    Single<EmbeddingResult> createEmbeddings(@Body EmbeddingRequest request);

    /**
     * 已弃用。
     * 创建嵌入向量。
     * 此方法用于通过指定的引擎ID生成嵌入向量。
     *
     * @param engineId 引擎的唯一标识符。
     * @param request  嵌入向量的请求体，包含生成嵌入向量的必要信息。
     * @return Single<EmbeddingResult> 返回一个Single对象，该对象封装了嵌入向量的结果。
     * @deprecated 使用此方法已不推荐，请使用新的API。
     */
    @Deprecated
    @POST("/v1/engines/{engine_id}/embeddings")
    Single<EmbeddingResult> createEmbeddings(@Path("engine_id") String engineId, @Body EmbeddingRequest request);

    /**
     * 列出文件列表。
     * 此方法用于获取OpenAI平台上所有文件的列表。
     *
     * @return Single<OpenAiResponse < File>>: 返回一个Single对象，其中包含OpenAiResponse对象，该对象封装了文件列表信息。
     */
    @GET("/v1/files")
    Single<OpenAiResponse<File>> listFiles();

    /**
     * 上传文件的接口。
     *
     * @param purpose 用于指定上传文件的目的，例如用途描述。
     * @param file    需要上传的文件。
     * @return Single<File> 返回一个Single对象，表示上传文件的响应结果。
     */
    @Multipart
    @POST("/v1/files")
    Single<File> uploadFile(@Part("purpose") RequestBody purpose, @Part MultipartBody.Part file);

    /**
     * 删除指定文件。
     *
     * @param fileId 要删除的文件的ID。
     * @return Single<DeleteResult> 一个Single对象，它封装了删除操作的结果。
     */
    @DELETE("/v1/files/{file_id}")
    Single<DeleteResult> deleteFile(@Path("file_id") String fileId);

    /**
     * 根据文件ID检索文件。
     *
     * @param fileId 文件ID，用于唯一标识一个文件。
     * @return Single<File> 返回一个Single对象，该对象封装了检索到的文件信息。
     */
    @GET("/v1/files/{file_id}")
    Single<File> retrieveFile(@Path("file_id") String fileId);

    /**
     * 检索指定文件ID的内容。
     *
     * @param fileId 文件的唯一标识符。
     * @return Single<ResponseBody> 返回一个Single对象，包含文件内容的ResponseBody。
     */
    @Streaming
    @GET("/v1/files/{file_id}/content")
    Single<ResponseBody> retrieveFileContent(@Path("file_id") String fileId);

    /**
     * 创建微调任务。
     * 此方法用于向服务器发送一个创建微调任务的请求。
     *
     * @param request 微调任务请求对象，包含创建微调任务所需的所有信息。
     * @return Single<FineTuningJob> 一个Single对象，用于异步获取创建的微调任务对象。
     */
    @POST("/v1/fine_tuning/jobs")
    Single<FineTuningJob> createFineTuningJob(@Body FineTuningJobRequest request);

    /**
     * 列出微调作业。
     * 此方法用于获取OpenAI平台上所有微调作业的信息。
     *
     * @return Single<OpenAiResponse < FineTuningJob>>: 返回一个Single对象，包含微调作业的响应信息。
     */
    @GET("/v1/fine_tuning/jobs")
    Single<OpenAiResponse<FineTuningJob>> listFineTuningJobs();

    /**
     * 根据细粒度训练作业ID获取细粒度训练作业的详细信息。
     *
     * @param fineTuningJobId 细粒度训练作业的唯一标识符。
     * @return Single<FineTuningJob> 一个Single对象，包含细粒度训练作业的详细信息。
     */
    @GET("/v1/fine_tuning/jobs/{fine_tuning_job_id}")
    Single<FineTuningJob> retrieveFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId);

    /**
     * 取消微调作业。
     * 此方法用于取消指定ID的微调作业。
     *
     * @param fineTuningJobId 要取消的微调作业的ID。
     * @return Single<FineTuningJob> 返回一个Single对象，该对象在操作完成后会发出一个FineTuningJob对象，表示取消作业的结果。
     */
    @POST("/v1/fine_tuning/jobs/{fine_tuning_job_id}/cancel")
    Single<FineTuningJob> cancelFineTuningJob(@Path("fine_tuning_job_id") String fineTuningJobId);

    /**
     * 列出特定微调作业的事件。
     *
     * @param fineTuningJobId 微调作业的ID。
     * @return 一个Single对象，包含OpenAiResponse类型的结果，其中结果包含FineTuningEvent类型的事件列表。
     */
    @GET("/v1/fine_tuning/jobs/{fine_tuning_job_id}/events")
    Single<OpenAiResponse<FineTuningEvent>> listFineTuningJobEvents(@Path("fine_tuning_job_id") String fineTuningJobId);

    /**
     * 创建微调模型。
     *
     * @param request 微调请求对象，包含创建微调模型所需的所有参数。
     * @return Single<FineTuneResult> 一个Single对象，表示微调模型的创建结果。
     * @deprecated 该方法已被弃用，请使用新的API接口。
     */
    @Deprecated
    @POST("/v1/fine-tunes")
    Single<FineTuneResult> createFineTune(@Body FineTuneRequest request);

    /**
     * 创建细粒度调优的自动完成请求。
     * 此方法用于向服务器发送一个细粒度调优的自动完成请求，并返回一个包含完成结果的Single对象。
     *
     * @param request 包含细粒度调优请求参数的CompletionRequest对象。
     * @return Single<CompletionResult> 一个Single对象，用于异步接收CompletionResult结果。
     */
    @POST("/v1/completions")
    Single<CompletionResult> createFineTuneCompletion(@Body CompletionRequest request);

    /**
     * 已弃用。
     * 用于获取所有微调模型的列表。
     *
     * @return Single<OpenAiResponse < FineTuneResult>> 返回一个Single对象，其中包含OpenAiResponse对象，该对象封装了FineTuneResult列表。
     * @deprecated 此方法已被弃用，请使用新的API接口。
     */
    @Deprecated
    @GET("/v1/fine-tunes")
    Single<OpenAiResponse<FineTuneResult>> listFineTunes();

    /**
     * 已弃用。
     * 获取指定微调模型的详细信息。
     *
     * @param fineTuneId 微调模型的ID。
     * @return Single<FineTuneResult> 返回一个Single对象，该对象封装了微调模型的详细信息。
     * @deprecated 请使用新的API端点代替此方法。
     */
    @Deprecated
    @GET("/v1/fine-tunes/{fine_tune_id}")
    Single<FineTuneResult> retrieveFineTune(@Path("fine_tune_id") String fineTuneId);

    /**
     * 取消微调任务。
     * <p>
     * 此方法已被弃用，建议使用其他方法来取消微调任务。
     *
     * @param fineTuneId 微调任务的唯一标识符。
     * @return Single<FineTuneResult> 返回一个Single对象，该对象在成功取消微调任务时发出FineTuneResult对象。
     * @deprecated 此方法已被弃用，请使用其他方法来取消微调任务。
     */
    @Deprecated
    @POST("/v1/fine-tunes/{fine_tune_id}/cancel")
    Single<FineTuneResult> cancelFineTune(@Path("fine_tune_id") String fineTuneId);

    /**
     * 已弃用。
     * 列出特定微调模型的训练事件。
     * 此方法用于获取指定微调ID的事件列表，这些事件记录了微调训练过程中的关键点。
     *
     * @param fineTuneId 微调模型的唯一标识符。
     * @return Single<OpenAiResponse < FineTuneEvent>> 返回一个Single对象，该对象包含微调事件列表的响应。
     */
    @Deprecated
    @GET("/v1/fine-tunes/{fine_tune_id}/events")
    Single<OpenAiResponse<FineTuneEvent>> listFineTuneEvents(@Path("fine_tune_id") String fineTuneId);

    /**
     * 删除细调模型。
     * 此方法用于删除指定ID的细调模型。
     *
     * @param fineTuneId 要删除的细调模型的ID。
     * @return Single<DeleteResult> 返回一个Single对象，包含删除操作的结果。
     */
    @DELETE("/v1/models/{fine_tune_id}")
    Single<DeleteResult> deleteFineTune(@Path("fine_tune_id") String fineTuneId);

    /**
     * 创建图像的接口调用。
     *
     * @param request 创建图像的请求体，包含生成图像所需的参数。
     * @return Single<ImageResult> 返回一个Single对象，该对象在成功生成图像后会发出一个ImageResult对象。
     */
    @POST("/v1/images/generations")
    Single<ImageResult> createImage(@Body CreateImageRequest request);

    /**
     * 创建图像编辑任务。
     * 此方法用于通过POST请求向服务器发送图像编辑请求，并返回编辑后的图像结果。
     *
     * @param requestBody 包含图像编辑请求数据的RequestBody对象。
     * @return Single<ImageResult> 返回一个Single对象，包含图像编辑后的结果。
     */
    @POST("/v1/images/edits")
    Single<ImageResult> createImageEdit(@Body RequestBody requestBody);

    /**
     * 创建图像变体。
     * 此方法用于通过POST请求向服务器发送图像变体创建请求。
     *
     * @param requestBody 包含创建图像变体所需数据的请求体。
     * @return Single<ImageResult> 返回一个Single对象，该对象在操作成功时发出ImageResult实例。
     */
    @POST("/v1/images/variations")
    Single<ImageResult> createImageVariation(@Body RequestBody requestBody);

    /**
     * 创建语音转写任务。
     * <p>
     * 此方法用于向服务器发送一个POST请求，请求路径为"/v1/audio/transcriptions"，
     * 请求体中包含语音转写的具体信息。
     *
     * @param requestBody 请求体，包含需要转写的音频信息。
     * @return Single<TranscriptionResult> 一个Single对象，用于异步接收转写结果。
     */
    @POST("/v1/audio/transcriptions")
    Single<TranscriptionResult> createTranscription(@Body RequestBody requestBody);

    /**
     * 创建音频翻译请求。
     * <p>
     * 此方法用于向服务器发送一个POST请求，请求路径为"/v1/audio/translations"，
     * 请求体中包含音频翻译的具体信息。
     *
     * @param requestBody 请求体，包含音频翻译所需的数据。
     * @return Single<TranslationResult> 返回一个Single对象，该对象在成功接收到服务器响应时，
     * 会发射一个TranslationResult对象，否则发射一个错误。
     */
    @POST("/v1/audio/translations")
    Single<TranslationResult> createTranslation(@Body RequestBody requestBody);

    /**
     * 创建语音请求。
     * 此方法用于发送语音请求到服务端，并返回响应体。
     *
     * @param requestBody 创建语音请求的请求体，包含必要的语音数据和参数。
     * @return Single<ResponseBody> 返回一个Single对象，包含服务器的响应体。
     */
    @POST("/v1/audio/speech")
    Single<ResponseBody> createSpeech(@Body CreateSpeechRequest requestBody);

    /**
     * 创建内容审核请求。
     * <p>
     * 此方法用于向服务器发送内容审核请求，并返回审核结果。
     *
     * @param request 审核请求对象，包含需要审核的内容及相关参数。
     * @return Single<ModerationResult> 返回一个Single对象，该对象在审核结果返回时发射ModerationResult对象。
     */
    @POST("/v1/moderations")
    Single<ModerationResult> createModeration(@Body ModerationRequest request);

    /**
     * 已弃用。获取OpenAI引擎列表的接口。
     * <p>
     * 此方法已不推荐使用，建议使用新的API接口获取引擎列表。
     *
     * @return Single<OpenAiResponse < Engine>>：返回一个Single对象，其中包含OpenAI引擎列表的响应。
     */
    @Deprecated
    @GET("v1/engines")
    Single<OpenAiResponse<Engine>> getEngines();

    /**
     * 已弃用。获取指定引擎的详细信息。
     *
     * @param engineId 引擎的唯一标识符
     * @return 返回一个Single对象，该对象在成功获取到引擎信息时发射一个Engine对象，否则发射一个错误。
     * @deprecated 此方法已被弃用，请使用新的API获取引擎信息。
     */
    @Deprecated
    @GET("/v1/engines/{engine_id}")
    Single<Engine> getEngine(@Path("engine_id") String engineId);

    /**
     * 查询订阅信息：包含总金额（美元）和其他订阅信息。
     *
     * <p>此方法已被弃用，请使用新的API接口。
     *
     * @return 返回订阅信息对象。
     */
    @Deprecated
    @GET("v1/dashboard/billing/subscription")
    Single<Subscription> subscription();

    /**
     * 查询账户调用接口消费金额。
     * 该方法返回指定日期范围内账户的总消费金额（单位为美分）。
     *
     * @param starDate 查询开始日期
     * @param endDate  查询结束日期
     * @return 消费金额信息
     */
    @Deprecated
    @GET("v1/dashboard/billing/usage")
    Single<BillingUsage> billingUsage(@Query("start_date") LocalDate starDate, @Query("end_date") LocalDate endDate);

    /**
     * 创建助理。
     * 此方法用于通过POST请求向服务器发送创建助理的请求。
     *
     * @param request 助理创建请求体，包含创建助理所需的信息。
     * @return Single<Assistant> 返回一个Single对象，它封装了创建助理的响应。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/assistants")
    Single<Assistant> createAssistant(@Body AssistantRequest request);

    /**
     * 检索指定ID的助手信息。
     *
     * @param assistantId 助手的唯一标识符。
     * @return Single<Assistant> 一个Single对象，它将发射一个Assistant对象，该对象包含助手的详细信息。
     * @Headers 用于指定HTTP请求头，此处指定OpenAI-Beta为assistants=v1。
     * @GET 指定HTTP请求方法为GET，并定义请求的URL路径。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/assistants/{assistant_id}")
    Single<Assistant> retrieveAssistant(@Path("assistant_id") String assistantId);

    /**
     * 修改助手信息。
     * 此方法用于更新指定ID的助手信息。
     *
     * @param assistantId 助手的唯一标识ID。
     * @param request     包含修改助手信息的请求体。
     * @return Single<Assistant> 返回一个Single对象，封装了修改后的助手信息。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/assistants/{assistant_id}")
    Single<Assistant> modifyAssistant(@Path("assistant_id") String assistantId, @Body ModifyAssistantRequest request);

    /**
     * 删除指定的助手。
     *
     * @param assistantId 要删除的助手的ID。
     * @return Single<DeleteResult> 一个Single对象，它会在删除操作完成后返回一个DeleteResult对象，
     * 其中包含删除操作的结果信息。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @DELETE("/v1/assistants/{assistant_id}")
    Single<DeleteResult> deleteAssistant(@Path("assistant_id") String assistantId);

    /**
     * 列出所有助手的接口。
     * <p>
     * 此方法用于获取所有助手的列表信息。
     *
     * @param filterRequest 过滤器请求参数，用于筛选助手列表。
     * @return Single<OpenAiResponse < Assistant>> 返回包含助手列表的响应对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/assistants")
    Single<OpenAiResponse<Assistant>> listAssistants(@QueryMap Map<String, Object> filterRequest);

    /**
     * 创建助手文件。
     * 该方法用于为指定的助手创建一个新的文件。
     *
     * @param assistantId 助手的唯一标识符。
     * @param fileRequest 包含要创建的文件信息的请求对象。
     * @return Single<AssistantFile> 返回一个Single对象，该对象在成功创建文件时发出AssistantFile对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/assistants/{assistant_id}/files")
    Single<AssistantFile> createAssistantFile(@Path("assistant_id") String assistantId, @Body AssistantFileRequest fileRequest);

    /**
     * 检索指定助手ID和文件ID的助手文件。
     *
     * @param assistantId 助手的唯一标识符。
     * @param fileId      文件的唯一标识符。
     * @return 返回一个Single对象，包含检索到的AssistantFile对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/assistants/{assistant_id}/files/{file_id}")
    Single<AssistantFile> retrieveAssistantFile(@Path("assistant_id") String assistantId, @Path("file_id") String fileId);

    /**
     * 删除助手文件。
     * 此方法用于删除指定助手的文件。
     *
     * @param assistantId 助手的唯一标识符。
     * @param fileId      要删除的文件的唯一标识符。
     * @return Single<DeleteResult> 一个Single对象，它将发射一个DeleteResult，表示删除操作的结果。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @DELETE("/v1/assistants/{assistant_id}/files/{file_id}")
    Single<DeleteResult> deleteAssistantFile(@Path("assistant_id") String assistantId, @Path("file_id") String fileId);

    /**
     * 列出助手文件。
     * 此方法用于获取指定助手ID下的所有文件列表。
     *
     * @param assistantId   助手的唯一标识符。
     * @param filterRequest 包含过滤条件的请求参数，例如页码、每页数量等。
     * @return Single<OpenAiResponse < AssistantFile>> 返回包含助手文件列表的响应对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/assistants/{assistant_id}/files")
    Single<OpenAiResponse<AssistantFile>> listAssistantFiles(@Path("assistant_id") String assistantId, @QueryMap Map<String, Object> filterRequest);

    /**
     * 创建新的线程。
     * <p>
     * 此方法用于通过指定的请求创建一个新的线程。
     *
     * @param request 包含创建线程所需信息的请求体。
     * @return Single<Thread> 返回一个Single对象，它封装了创建的线程。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/threads")
    Single<Thread> createThread(@Body ThreadRequest request);

    /**
     * 检索指定线程的详细信息。
     *
     * @param threadId 线程的唯一标识符。
     * @return 返回一个包含线程详细信息的{@link Single}对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}")
    Single<Thread> retrieveThread(@Path("thread_id") String threadId);

    /**
     * 修改指定线程的信息。
     *
     * @param threadId 线程的唯一标识符。
     * @param request  包含要修改的线程信息的请求体。
     * @return Single<Thread> 返回一个Single对象，该对象封装了修改后的线程信息。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/threads/{thread_id}")
    Single<Thread> modifyThread(@Path("thread_id") String threadId, @Body ThreadRequest request);

    /**
     * 删除指定的线程。
     *
     * @param threadId 要删除的线程的ID。
     * @return Single<DeleteResult> 一个Single对象，它将在操作完成后返回一个DeleteResult对象，
     * 其中包含操作的结果信息。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @DELETE("/v1/threads/{thread_id}")
    Single<DeleteResult> deleteThread(@Path("thread_id") String threadId);


    /**
     * 创建消息。
     * <p>
     * 此方法用于向指定的线程ID发送消息。
     *
     * @param threadId 线程ID，用于指定消息发送的目标线程。
     * @param request  消息请求对象，包含要发送的消息内容及其他相关参数。
     * @return Single<Message> 返回一个Single对象，该对象在成功创建消息后会发出包含消息内容的Message对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/threads/{thread_id}/messages")
    Single<Message> createMessage(@Path("thread_id") String threadId, @Body MessageRequest request);

    /**
     * 检索指定线程和消息ID的消息。
     *
     * @param threadId  线程的ID。
     * @param messageId 消息的ID。
     * @return Single<Message> 单个包含消息的Observable对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}/messages/{message_id}")
    Single<Message> retrieveMessage(@Path("thread_id") String threadId, @Path("message_id") String messageId);

    /**
     * 修改指定线程和消息ID的消息内容。
     *
     * @param threadId  线程ID，用于标识消息所在的线程。
     * @param messageId 消息ID，用于标识要修改的消息。
     * @param request   修改消息的请求体，包含要更新的消息内容。
     * @return Single<Message> 返回一个Single对象，该对象在操作成功时将包含修改后的消息内容。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @POST("/v1/threads/{thread_id}/messages/{message_id}")
    Single<Message> modifyMessage(@Path("thread_id") String threadId, @Path("message_id") String messageId, @Body ModifyMessageRequest request);

    /**
     * 列出指定线程的消息。
     *
     * @param threadId 线程的唯一标识符。
     * @return 一个包含消息列表的{@link Single}对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}/messages")
    Single<OpenAiResponse<Message>> listMessages(@Path("thread_id") String threadId);

    /**
     * 列出指定线程的消息。
     *
     * @param threadId      线程的唯一标识符。
     * @param filterRequest 包含过滤条件的请求参数，例如分页信息等。
     * @return 返回一个包含消息列表的响应对象。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}/messages")
    Single<OpenAiResponse<Message>> listMessages(@Path("thread_id") String threadId, @QueryMap Map<String, Object> filterRequest);

    /**
     * 检索指定线程、消息和文件的详细信息。
     *
     * @param threadId  线程的唯一标识符。
     * @param messageId 消息的唯一标识符。
     * @param fileId    文件的唯一标识符。
     * @return Single<MessageFile> 一个Single对象，包含检索到的消息文件信息。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}/messages/{message_id}/files/{file_id}")
    Single<MessageFile> retrieveMessageFile(@Path("thread_id") String threadId, @Path("message_id") String messageId, @Path("file_id") String fileId);

    /**
     * 列出指定消息的文件。
     * <p>
     * 此方法用于获取指定线程和消息ID下的所有文件列表。
     *
     * @param threadId  线程ID。
     * @param messageId 消息ID。
     * @return {@link Single<OpenAiResponse<MessageFile>>} 包含文件列表的响应。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}/messages/{message_id}/files")
    Single<OpenAiResponse<MessageFile>> listMessageFiles(@Path("thread_id") String threadId, @Path("message_id") String messageId);

    /**
     * 列出指定消息的附件文件。
     *
     * @param threadId      线程ID，用于唯一标识一个对话线程。
     * @param messageId     消息ID，用于唯一标识一个消息。
     * @param filterRequest 过滤器请求，用于指定筛选条件，例如限制返回的文件类型等。
     * @return Single<OpenAiResponse < MessageFile>> 返回一个Single对象，包含OpenAI的响应，其中包含消息的附件文件信息。
     */
    @Headers({"OpenAI-Beta: assistants=v1"})
    @GET("/v1/threads/{thread_id}/messages/{message_id}/files")
    Single<OpenAiResponse<MessageFile>> listMessageFiles(@Path("thread_id") String threadId, @Path("message_id") String messageId, @QueryMap Map<String, Object> filterRequest);

    /**
     * 创建运行实例。
     * <p>
     * 此方法用于在指定的线程ID下创建一个新的运行实例。
     *
     * @param threadId         线程ID，用于指定在哪个线程下创建运行实例。
     * @param runCreateRequest 运行创建请求，包含创建运行实例所需的各种参数和配置。
     * @return Single<Run> 返回一个Single对象，该对象在运行时将包含创建的运行实例信息。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @POST("/v1/threads/{thread_id}/runs")
    Single<Run> createRun(@Path("thread_id") String threadId, @Body RunCreateRequest runCreateRequest);

    /**
     * 检索特定线程的运行实例。
     *
     * @param threadId 线程的唯一标识符。
     * @param runId    运行的唯一标识符。
     * @return Single<Run> 返回一个Single对象，该对象封装了对应线程的运行实例。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @GET("/v1/threads/{thread_id}/runs/{run_id}")
    Single<Run> retrieveRun(@Path("thread_id") String threadId, @Path("run_id") String runId);

    /**
     * 修改指定线程的运行实例。
     *
     * @param threadId 线程的唯一标识符。
     * @param runId    运行实例的唯一标识符。
     * @param metadata 包含要修改的运行实例元数据的Map。
     * @return Single<Run> 一个Single对象，用于异步获取修改后的运行实例。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @POST("/v1/threads/{thread_id}/runs/{run_id}")
    Single<Run> modifyRun(@Path("thread_id") String threadId, @Path("run_id") String runId, @Body Map<String, String> metadata);

    /**
     * 列出指定线程的运行记录。
     *
     * @param threadId             线程的唯一标识符。
     * @param listSearchParameters 查询参数的映射，用于过滤和排序运行记录。
     * @return 一个Single类型的{@link OpenAiResponse}对象，包含运行记录的列表。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @GET("/v1/threads/{thread_id}/runs")
    Single<OpenAiResponse<Run>> listRuns(@Path("thread_id") String threadId, @QueryMap Map<String, String> listSearchParameters);


    /**
     * 提交工具输出到指定的线程和运行ID。
     *
     * @param threadId                 线程ID，用于标识目标线程。
     * @param runId                    运行ID，用于标识目标运行。
     * @param submitToolOutputsRequest 提交工具输出的请求体，包含需要提交的工具输出数据。
     * @return 返回一个Single对象，表示异步操作的结果，其中包含运行信息。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @POST("/v1/threads/{thread_id}/runs/{run_id}/submit_tool_outputs")
    Single<Run> submitToolOutputs(@Path("thread_id") String threadId, @Path("run_id") String runId, @Body SubmitToolOutputsRequest submitToolOutputsRequest);


    /**
     * 取消一个运行中的线程。
     *
     * @param threadId 线程的唯一标识符。
     * @param runId    要取消的运行的唯一标识符。
     * @return 一个Single对象，它发射一个Run对象，表示取消操作的结果。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @POST("/v1/threads/{thread_id}/runs/{run_id}/cancel")
    Single<Run> cancelRun(@Path("thread_id") String threadId, @Path("run_id") String runId);

    /**
     * 创建线程并运行。
     * <p>
     * 此方法用于通过指定的请求创建一个新的线程并立即运行。
     *
     * @param createThreadAndRunRequest 创建线程并运行请求的请求体。
     * @return Single<Run> 一个Single对象，代表运行结果的Observable。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @POST("/v1/threads/runs")
    Single<Run> createThreadAndRun(@Body CreateThreadAndRunRequest createThreadAndRunRequest);

    /**
     * 检索特定线程、运行和步骤的详细信息。
     *
     * @param threadId 线程的唯一标识符。
     * @param runId    运行的唯一标识符。
     * @param stepId   步骤的唯一标识符。
     * @return 一个包含运行步骤详细信息的Single对象。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @GET("/v1/threads/{thread_id}/runs/{run_id}/steps/{step_id}")
    Single<RunStep> retrieveRunStep(@Path("thread_id") String threadId, @Path("run_id") String runId, @Path("step_id") String stepId);

    /**
     * 列出指定线程和运行ID下的所有步骤。
     *
     * @param threadId             线程ID，用于唯一标识一个线程。
     * @param runId                运行ID，用于唯一标识一个运行实例。
     * @param listSearchParameters 搜索参数，用于过滤和排序步骤列表。
     * @return 返回一个Single对象，包含OpenAiResponse类型的RunStep对象列表。
     */
    @Headers("OpenAI-Beta: assistants=v1")
    @GET("/v1/threads/{thread_id}/runs/{run_id}/steps")
    Single<OpenAiResponse<RunStep>> listRunSteps(@Path("thread_id") String threadId, @Path("run_id") String runId, @QueryMap Map<String, String> listSearchParameters);
}
