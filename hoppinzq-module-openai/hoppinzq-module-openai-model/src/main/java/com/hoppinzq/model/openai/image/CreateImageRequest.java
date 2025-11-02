package com.hoppinzq.model.openai.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * https://beta.openai.com/docs/api-reference/images/create
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageRequest {
    @NonNull
    String prompt;
    //dall-e-2
    String model;
    Integer n;
    String quality;
    String size;
    @JsonProperty("response_format")
    String responseFormat;
    String style;
    String user;
}
