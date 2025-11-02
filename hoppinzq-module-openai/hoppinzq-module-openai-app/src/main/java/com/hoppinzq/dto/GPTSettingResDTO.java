package com.hoppinzq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GPTSettingResDTO {
    private Long id;
    private String gptUrl;
    private Long userId;
    private String model;
}

