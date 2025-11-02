package com.hoppinzq.model.openai.audio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * https://platform.openai.com/docs/api-reference/audio/create
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateTranscriptionRequest {
    @NonNull
    String model;
    String prompt;
    @JsonProperty("response_format")
    String responseFormat;
    Double temperature;
    String language;
}
