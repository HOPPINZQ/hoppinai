package com.hoppinzq.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ModelQueryDTO extends GPTBaseDTO {

    @Builder(toBuilder = true)
    public ModelQueryDTO(String proxyUrl, String apikey) {
        super(proxyUrl, apikey);
    }
}

