package com.hoppinzq.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GPTBaseDTO {
    protected String proxyUrl;
    protected String apikey;

    public GPTBaseDTO(String proxyUrl, String apikey) {
        this.proxyUrl = proxyUrl;
        this.apikey = apikey;
    }
}

