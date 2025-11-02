package com.hoppinzq.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GPTSettingInsertDTO extends GPTBaseDTO {
    private String model;

    @Builder(toBuilder = true)
    public GPTSettingInsertDTO(String proxyUrl, String apikey, String model) {
        super(proxyUrl, apikey);
        this.model = model;
    }
}

