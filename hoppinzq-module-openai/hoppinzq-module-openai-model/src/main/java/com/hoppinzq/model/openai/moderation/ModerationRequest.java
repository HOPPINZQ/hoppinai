package com.hoppinzq.model.openai.moderation;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class ModerationRequest {
    @NonNull
    private String input;
    private String model;
}
