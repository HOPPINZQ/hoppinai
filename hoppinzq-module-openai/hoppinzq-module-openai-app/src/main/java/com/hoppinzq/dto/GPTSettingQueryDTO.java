package com.hoppinzq.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GPTSettingQueryDTO {
    private Long id;
    private String gptUrl;
    private String gptApikey;
    private Long userId;
}

