package com.hoppinzq.function.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.hoppinzq.function.zq.constants.AiFunctionCallResponse;
import com.hoppinzq.function.zq.constants.FunctionCallCommon;
import com.hoppinzq.model.openai.image.CreateImageRequest;
import com.hoppinzq.model.openai.image.ImageResult;
import com.hoppinzq.openai.service.OpenAiService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class ImageFunctionCall extends AiFunctionCallResponse {

    public ImageFunctionCall(String prompt, Integer num, String size, String model) {
        try {
            OpenAiService service = FunctionCallCommon.serviceThreadLocal.get();
            ImageResult image = service.createImage(CreateImageRequest.builder().prompt(prompt).n(num).size(size).model(model).build());
            List<String> imageUrl = new ArrayList(image.getData().size());
            image.getData().forEach(imageData -> {
                imageUrl.add(imageData.getUrl());
            });
            success(imageUrl.toString());
        } catch (Exception e) {
            e.printStackTrace();
            fail("生成图片失败：" + e.getMessage());
        } finally {

        }
    }

    public static class ImageFunctionCallRequest {
        @JsonPropertyDescription("图片提示词，输出英文，尽量优化一下")
        @JsonProperty(required = true)
        public String prompt;

        @JsonPropertyDescription("图片张数，默认是1")
        public Integer num = 1;

        @JsonPropertyDescription("图片宽高，只能是 1024x1024, 1024x1792 或 1792x1024")
        @JsonProperty(required = true)
        public String size = "1024x1024";

        @JsonPropertyDescription("图片生成模型，默认是dall-e-3，支持dall-e-2和dall-e-3")
        @JsonProperty(required = true)
        public String model = "dall-e-3";
    }

}
