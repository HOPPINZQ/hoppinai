package com.hoppinzq.model.openai.audio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CreateSpeechRequest {
    @NonNull
    String model;
    @NonNull
    String input;
    @NonNull
    String voice;
    @JsonProperty("response_format")
    String responseFormat;
    Double speed;
}
