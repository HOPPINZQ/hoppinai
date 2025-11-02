package com.hoppinzq.model.openai.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * https://beta.openai.com/docs/api-reference/images
 */
@Data
public class Image {
    String url;
    @JsonProperty("b64_json")
    String b64Json;
    @JsonProperty("revised_prompt")
    String revisedPrompt;
}
