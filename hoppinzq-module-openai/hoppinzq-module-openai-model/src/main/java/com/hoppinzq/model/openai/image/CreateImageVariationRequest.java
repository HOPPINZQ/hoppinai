package com.hoppinzq.model.openai.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * https://beta.openai.com/docs/api-reference/images/create-variation
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateImageVariationRequest {
    Integer n;
    String model;
    String size;
    @JsonProperty("response_format")
    String responseFormat;
    String user;
}
