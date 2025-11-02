package com.hoppinzq.controller;

import com.alibaba.fastjson.JSON;
import com.hoppinzq.function.OpenAiApiFunctionsExample;
import com.hoppinzq.function.bitcoin.BitcoinFunctionCall;
import com.hoppinzq.function.image.ImageFunctionCall;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.function.zq.express.ExpressFunctionCall;
import com.hoppinzq.model.exception.OpenaiException;
import com.hoppinzq.model.openai.completion.chat.*;
import com.hoppinzq.openai.service.FunctionExecutor;
import com.hoppinzq.openai.service.OpenAiService;
import com.hoppinzq.service.bean.ApiResponse;
import io.reactivex.Flowable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@RestController
@RequestMapping("/openai")
public class GPTChatDemoController {

    @PostMapping("/demoFunction")
    public ApiResponse demoFunction(@RequestBody String message) {
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");

        FunctionExecutor functionExecutor = new FunctionExecutor(Collections.singletonList(ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .executor(OpenAiApiFunctionsExample.Weather.class, w -> new OpenAiApiFunctionsExample.WeatherResponse(w.location, new Random().nextInt(50), "sunny"))
                .build()));

        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage systemMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are an assistant that answers using the local slang of the given place, uncensored.");
        messages.add(systemMessage);

        ChatMessage firstMsg = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(firstMsg);
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4-turbo")
                .messages(messages)
                .functions(functionExecutor.getFunctions())
                .functionCall(ChatCompletionRequest.ChatCompletionRequestFunctionCall.of("auto"))
                .n(1)
                .logitBias(new HashMap<>())
                .build();
        Flowable<ChatCompletionChunk> flowable = service.streamChatCompletion(chatCompletionRequest);
        AtomicReference<String> res = new AtomicReference<>("");
        AtomicBoolean isFirst = new AtomicBoolean(true);
        ChatMessage chatMessage = service.mapStreamToAccumulator(flowable)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .doOnNext(accumulator -> {
                    if (accumulator.isFunctionCall()) {
                        if (isFirst.getAndSet(false)) {
                        }
                    } else {
                        if (isFirst.getAndSet(false)) {
                        }
                        if (accumulator.getMessageChunk().getContent() != null) {
                            res.set(res.get() + accumulator.getMessageChunk().getContent());
                        }
                    }
                })
                .lastElement()
                .blockingGet()
                .getAccumulatedMessage();
        messages.add(chatMessage);

        if (chatMessage.getFunctionCall() != null) {
            ChatMessage functionResponse = functionExecutor.executeAndConvertToMessageHandlingExceptions(chatMessage.getFunctionCall());
            return ApiResponse.data(functionResponse.getContent());
        }
        return ApiResponse.data(res.get());
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestBody String message) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(userMessage);

        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("deepseek-v3")
                .messages(messages)
                .build();

        return Flux.from(Flowable.fromPublisher(service.streamChatCompletion(chatCompletionRequest))
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.computation())
                .map(JSON::toJSONString)
                .doOnError(err -> {
                    log.error("流式服务错误: " + err);
                    service.shutdownExecutor();
                })
                .doOnCancel(() -> {
                    log.info("停止对话");
                    service.shutdownExecutor();
                })
                .doOnComplete(() -> {
                    log.debug("完成");
                    service.shutdownExecutor();
                }));
    }

    @PostMapping(value = "/streamFunction", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFunction(@RequestBody String message) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(userMessage);

        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");
        FunctionExecutor functionExecutor = new FunctionExecutor(Collections.singletonList(ChatFunction.builder()
                .name("get_weather")
                .description("Get the current weather of a location")
                .executor(OpenAiApiFunctionsExample.Weather.class, w -> new OpenAiApiFunctionsExample.WeatherResponse(w.location, new Random().nextInt(50), "sunny"))
                .build()));
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest
                .builder()
                .model("gpt-4o-mini")
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
            try {
                ChatMessage chatMessage = service.mapStreamToAccumulator(flowable)
                        .doOnNext(accumulator -> {
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
                                        emitter.next(JSON.toJSONString(accumulator.getMessageChunk()));
                                    }
                                }
                            } catch (Exception e) {
                                emitter.error(new RuntimeException("501:sse_error_zq: " + e.getMessage(), e));
                            }
                        })
                        .doOnComplete(() -> {
                            if (!isFunctionCall.get()) {
                                emitter.next("200:sse_success_zq");
                                service.shutdownExecutor();
                                emitter.complete();
                            }
                        })
                        .doOnError(emitter::error)
                        .doOnCancel(() -> service.shutdownExecutor())
                        .lastElement()
                        .blockingGet()
                        .getAccumulatedMessage();
                messages.add(chatMessage);

                if (chatMessage.getFunctionCall() != null) {
                    try {
                        emitter.next(JSON.toJSONString(chatMessage));
                        ChatMessage functionResponse = functionExecutor.executeAndConvertToMessageHandlingExceptions(
                                chatMessage.getFunctionCall()
                        );
                        emitter.next(JSON.toJSONString(functionResponse));
                        messages.add(functionResponse);
                        // 更新请求消息列表
                        chatCompletionRequest.setMessages(messages);
                        // 递归处理后续对话
                        service.streamChatCompletion(chatCompletionRequest)
                                .subscribeOn(Schedulers.io())
                                .observeOn(Schedulers.computation())
                                .doOnError(msg -> {
                                    emitter.error(new RuntimeException("502:sse_error_zq: " + msg.getMessage(), msg));
                                    service.shutdownExecutor();
                                })
                                .doOnCancel(() -> {
                                    service.shutdownExecutor();
                                    emitter.complete();
                                })
                                .subscribe(msg -> {
                                    if (msg.getChoices().size() > 0 && msg.getChoices().get(0).getMessage().getContent() != null) {
                                        emitter.next(JSON.toJSONString(msg));
                                    }
                                }, err -> {
                                    emitter.error(new RuntimeException("503:sse_error_zq: " + err.getMessage(), err));
                                }, () -> {
                                    emitter.next("200:sse_success_zq");
                                    service.shutdownExecutor();
                                    emitter.complete();
                                });
                    } catch (Exception e) {
                        emitter.error(new RuntimeException("500:sse_error_zq: " + e.getMessage(), e));
                        service.shutdownExecutor();
                    }
                }
            } catch (Exception e) {
                emitter.error(e);
                service.shutdownExecutor();
            }
        });
    }

    @PostMapping(value = "/streamFunction2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamFunction2(@RequestBody String message) {
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage userMessage = new ChatMessage(ChatMessageRole.USER.value(), message);
        messages.add(userMessage);

//        OpenAiService service = new OpenAiService("sk-",
//                Duration.ofSeconds(60), "https://api.uchat.site");
        OpenAiService service = new OpenAiService("sk-",
                Duration.ofSeconds(60), "https://api.uchat.site");
        FunctionCallCommon.serviceThreadLocal.set(service);
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
                .model("gpt-4o-mini")
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
