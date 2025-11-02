package com.hoppinzq.controller;

import com.alibaba.fastjson.JSON;
import com.hoppinzq.api.OssService;
import com.hoppinzq.dal.po.ChatMessagePO;
import com.hoppinzq.dal.po.ChatPO;
import com.hoppinzq.dal.po.GPTSettingPO;
import com.hoppinzq.dto.chat.ChatRequestDTO;
import com.hoppinzq.function.bitcoin.BitcoinFunctionCall;
import com.hoppinzq.function.image.ImageFunctionCall;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.function.zq.express.ExpressFunctionCall;
import com.hoppinzq.model.exception.OpenaiException;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.openai.service.FunctionExecutor;
import com.hoppinzq.openai.service.OpenAiService;
import com.hoppinzq.service.bean.ApiResponse;
import com.hoppinzq.service.bean.ZqServerConfig;
import com.hoppinzq.service.chatgpt.GPTChatService;
import com.hoppinzq.service.common.UserPrincipal;
import com.hoppinzq.service.gptsetting.GPTSettingService;
import com.hoppinzq.service.proxy.ServiceProxyFactory;
import com.hoppinzq.service.util.StringUtil;
import com.hoppinzq.service.util.UUIDUtil;
import com.hoppinzq.utils.RedisUtils;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
@RequestMapping("/openai")
public class GPTChatController {

    @Autowired
    private GPTSettingService gptSettingService;
    @Autowired
    private GPTChatService gptChatService;
    @Autowired
    private RedisUtils redisUtils;
    @Resource
    private ZqServerConfig zqServerConfig;

    /**
     * 通过SSE(Server-Sent Events)发送消息。
     *
     * @param message   用户输入的消息内容。
     * @param chatId    对话的唯一标识符。
     * @param settingId 对话设置的唯一标识符。
     * @param userId    用户的唯一标识符。
     * @return SseEmitter 对象，用于发送SSE消息。
     * @throws IOException
     */
    @GetMapping("/chatDeepSeek")
    public SseEmitter chatDeepSeek(String message, String chatId, String settingId, String userId) throws IOException {
        SseEmitter emitter = new SseEmitter(-1L);
        List<ChatMessage> messages = new ArrayList<>();
        try {
            if (settingId == null || "null".equals(settingId)) {
                settingId = "7";
            }
            if (userId == null) {
                throw new OpenaiException("userId不能为空！");
            }
            GPTSettingPO gptSettingPO = null;
            if (redisUtils.hasKey("openai:" + userId + ":" + settingId)) {
                Object redisGptSetting = redisUtils.get("openai:" + userId + ":" + settingId);
                gptSettingPO = JSON.parseObject(String.valueOf(redisGptSetting), GPTSettingPO.class);
            } else {
                gptSettingPO = gptSettingService.getSettingById(Long.parseLong(settingId), Long.parseLong(userId));
                // 缓存10分钟
                redisUtils.set("openai:" + userId + ":" + settingId, JSON.toJSONString(gptSettingPO), 600);
            }
            if (StringUtil.isNotEmpty(chatId)) {
                ChatPO chat = gptChatService.getChatByChatId(chatId);
                if (chat != null) {
                    List<ChatMessagePO> chatMessageList = gptChatService.getChatMessageByChatId(chatId, chat.getChat_context());
                    if (!"".equals(chat.getChat_system())) {
                        ChatMessage systemMessage = new ChatMessage("system", chat.getChat_system());
                        messages.add(systemMessage);
                    }
                    for (int i = chatMessageList.size() - 1; i >= 0; i--) {
                        ChatMessagePO chatMessagePO = chatMessageList.get(i);
                        ChatMessage chatMessage = new ChatMessage(chatMessagePO.getMessage_role(), chatMessagePO.getMessage());
                        messages.add(chatMessage);
                    }
                }
            }
            ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
            messages.add(userMessage);

            OpenAiService service = new OpenAiService(gptSettingPO.getGptApikey(),
                    Duration.ofSeconds(gptSettingPO.getTimeout()),
                    gptSettingPO.getGptUrl());
            ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                    .builder()
                    .model(gptSettingPO.getModel())
                    .temperature(0.6)
                    .messages(messages)
                    .build();
            service.streamChatCompletion(chatCompletionRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(Schedulers.computation())
                    .doOnError(msg -> {
                        log.error("sse服务错误");
                        service.shutdownExecutor();
                    })
                    .doOnCancel(() -> {
                        log.info("停止对话");
                        service.shutdownExecutor();
                    })
                    .subscribe(msg -> {
                        emitter.send(SseEmitter.event().name("message").data(msg));
                    }, err -> {
                        log.error("sse服务错误，" + err);
                        emitter.completeWithError(err);
                    }, () -> {
                        log.debug("完成");
                        emitter.send("200:sse_success_zq");
                        emitter.complete();
                        service.shutdownExecutor();
                    });
        } catch (Exception ex) {
            String logId = UUIDUtil.getUUID();
            log.error("------------------------------------------------");
            log.error("日志ID：" + logId);
            ex.printStackTrace();
            log.error("------------------------------------------------");
            emitter.send("500:sse_error_zq,zhangqiff19," + logId);
        }
        return emitter;
    }


    /**
     * 上传文件到对象存储服务。
     * <p>
     * 此方法接收一个MultipartFile对象作为文件输入，使用ZQServerConfig配置的用户名和密码创建一个UserPrincipal对象，
     * 然后通过ServiceProxyFactory创建OssService代理，用于上传文件到指定的对象存储服务地址。
     * 上传成功后，返回文件的URL地址。
     *
     * @param file 要上传的文件
     * @return ApiResponse 包含文件URL的响应对象
     * @throws IOException 如果在读取文件输入流或上传文件时发生I/O错误
     */
    @PostMapping("/uploadFile")
    public ApiResponse fileUpload(MultipartFile file) throws IOException {
        UserPrincipal upp = new UserPrincipal(zqServerConfig.getUserName(),
                zqServerConfig.getPassword());
        OssService ossService = ServiceProxyFactory.createProxy(OssService.class, zqServerConfig.getFileAddr(), upp);
        InputStream inputStream = file.getInputStream();
        String fileUrl = ossService.uploadFile(inputStream);
        return ApiResponse.data(fileUrl);
    }

    @PostMapping(value = "/streamChat", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFunction(@RequestBody ChatRequestDTO chatRequestDTO, HttpServletRequest request) {
        String message = chatRequestDTO.getMessage();
        String systemPrompt = chatRequestDTO.getSystemPrompt();
        String chatId = request.getHeader("chatId");
        String settingId = request.getHeader("settingId");
        String userId = request.getHeader("userId");
        if (settingId == null) {
            settingId = "7";
        }
        if (userId == null) {
            throw new OpenaiException("userId不能为空！");
        }
        GPTSettingPO gptSettingPO = null;
        List<ChatMessage> messages = new ArrayList<>();
        if (redisUtils.hasKey("openai:" + userId + ":" + settingId)) {
            Object redisGptSetting = redisUtils.get("openai:" + userId + ":" + settingId);
            gptSettingPO = JSON.parseObject(String.valueOf(redisGptSetting), GPTSettingPO.class);
        } else {
            gptSettingPO = gptSettingService.getSettingById(Long.parseLong(settingId), Long.parseLong(userId));
            // 缓存10分钟
            redisUtils.set("openai:" + userId + ":" + settingId, JSON.toJSONString(gptSettingPO), 600);
        }
        if (StringUtil.isNotEmpty(chatId)) {
            ChatPO chat = gptChatService.getChatByChatId(chatId);
            if (chat != null) {
                List<ChatMessagePO> chatMessageList = gptChatService.getChatMessageByChatId(chatId, chat.getChat_context());
                if (!"".equals(chat.getChat_system())) {
                    ChatMessage systemMessage = new ChatMessage("system", chat.getChat_system());
                    messages.add(systemMessage);
                }

                for (int i = chatMessageList.size() - 1; i >= 0; i--) {
                    ChatMessagePO chatMessagePO = chatMessageList.get(i);
                    ChatMessage chatMessage = new ChatMessage(chatMessagePO.getMessage_role(), chatMessagePO.getMessage());
                    messages.add(chatMessage);
                }
            }
        }
        if (systemPrompt != null && !"".equals(systemPrompt)) {
            ChatMessage systemMessage = new ChatMessage("system", systemPrompt);
            messages.add(systemMessage);
        }
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(userMessage);

        OpenAiService service = new OpenAiService(gptSettingPO.getGptApikey(),
                Duration.ofSeconds(60), gptSettingPO.getGptUrl());
        FunctionExecutor functionExecutor = new FunctionExecutor(Collections.singletonList(ChatFunction.builder()
                .name("get_express")
                .description("获取快递信息")
                .executor(ExpressFunctionCall.ExpressFunctionCallRequest.class, express -> new ExpressFunctionCall(express.expressId, express.expressCompanyCode))
                .build()));
        ChatFunction imageFunction = ChatFunction.builder()
                .name("generate_image")
                .description("生成图片")
                .executor(ImageFunctionCall.ImageFunctionCallRequest.class, image -> new ImageFunctionCall(image.prompt, image.num, image.size, image.model))
                .build();
        functionExecutor.addFunction(imageFunction);
        ChatFunction btnFunction = ChatFunction.builder()
                .name("get_btn_data")
                .description("获取近一个月比特币数据")
                .executor(BitcoinFunctionCall.BitcoinFunctionCallRequest.class, btn -> new BitcoinFunctionCall(btn.date))
                .build();
        functionExecutor.addFunction(btnFunction);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model(gptSettingPO.getModel())
                .messages(messages)
                .functions(functionExecutor.getFunctions())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .n(1)
                .logitBias(new HashMap<>())
                .build();
        Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(chatCompletionRequest);
        return Flux.create(emitter -> {
            AtomicBoolean isFirst = new AtomicBoolean(true);
            AtomicBoolean isFunctionCall = new AtomicBoolean(false);
            CompositeDisposable disposables = new CompositeDisposable();
            AtomicReference<ChatMessage> lastMessage = new AtomicReference<>();
            Disposable initialDisposable = service.mapStreamToAccumulator(flowable)
                    .subscribe(
                            accumulator -> {
                                lastMessage.set(accumulator.getAccumulatedMessage());
                                try {
                                    if (accumulator.isFunctionCall()) {
                                        isFunctionCall.set(true);
                                        if (isFirst.getAndSet(false)) {
                                        }
                                    } else {
                                        if (isFirst.getAndSet(false)) {
                                            if (accumulator.getMessageChunk().getFunctionCall() != null) {
                                                isFunctionCall.set(true);
                                            }
                                        }
                                        if (accumulator.getMessageChunk().getContent() != null) {
                                            isFunctionCall.set(false);
                                            ChatMessage messageChunk = accumulator.getMessageChunk();
                                            messageChunk.setRole("assistant");
                                            emitter.next(JSON.toJSONString(messageChunk));
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    emitter.error(new OpenaiException("501:sse_error_zq: " + e.getMessage(), e));
                                }
                            }, error -> {
                                error.printStackTrace();
                                emitter.error(new OpenaiException("500:sse_error_zq: " + error.getMessage(), error));
                                service.shutdownExecutor();
                                disposables.dispose();
                            }, () -> {
                                ChatMessage chatMessage = lastMessage.get();
                                if (chatMessage != null && chatMessage.getFunctionCall() != null) {
                                    try {
                                        emitter.next(JSON.toJSONString(chatMessage));
                                        ChatMessage functionResponse = functionExecutor.executeAndConvertToMessageHandlingExceptions(chatMessage.getFunctionCall());
                                        emitter.next(JSON.toJSONString(functionResponse));
                                        messages.add(functionResponse);
                                        chatCompletionRequest.setMessages(messages);
                                        Disposable followupDisposable = service.streamChatCompletion(chatCompletionRequest)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(Schedulers.computation())
                                                .subscribe(
                                                        msg -> {
                                                            if (msg.getChoices().size() > 0 &&
                                                                    msg.getChoices().get(0).getMessage().getContent() != null) {
                                                                ChatMessage functionCallChatMessage = msg.getChoices().get(0).getMessage();
                                                                functionCallChatMessage.setRole("assistant");
                                                                emitter.next(JSON.toJSONString(functionCallChatMessage));
                                                            }
                                                        }, error -> {
                                                            error.printStackTrace();
                                                            emitter.error(new OpenaiException("502:sse_error_zq: " + error.getMessage(), error));
                                                            service.shutdownExecutor();
                                                            disposables.dispose();
                                                        }, () -> {
                                                            emitter.next(JSON.toJSONString(ChatMessage.builder().content("200:sse_success_zq").role("assistant").is_end(true).build()));
                                                            service.shutdownExecutor();
                                                            emitter.complete();
                                                            disposables.dispose();
                                                        }
                                                );
                                        disposables.add(followupDisposable);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        emitter.error(new OpenaiException("503:sse_error_zq: " + e.getMessage(), e));
                                        service.shutdownExecutor();
                                        disposables.dispose();
                                    }
                                } else {
                                    emitter.next(JSON.toJSONString(ChatMessage.builder().content("200:sse_success_zq").role("assistant").is_end(true).build()));
                                    service.shutdownExecutor();
                                    emitter.complete();
                                    disposables.dispose();
                                }
                            }
                    );
            disposables.add(initialDisposable);
            // 用户取消了聊天
            emitter.onCancel(service::shutdownExecutor);
            emitter.onDispose(() -> {
                service.shutdownExecutor();
                disposables.dispose();
                FunctionCallCommon.serviceThreadLocal.remove();
            });
        });
    }
}

