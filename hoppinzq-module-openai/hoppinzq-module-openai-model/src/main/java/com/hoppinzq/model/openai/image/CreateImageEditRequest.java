package com.hoppinzq.model.openai.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * https://beta.openai.com/docs/api-reference/images/create-edit
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageEditRequest {
    @NonNull
    String prompt;
    String model;
    Integer n;
    String size;
    @JsonProperty("response_format")
    String responseFormat;
    String user;
}
