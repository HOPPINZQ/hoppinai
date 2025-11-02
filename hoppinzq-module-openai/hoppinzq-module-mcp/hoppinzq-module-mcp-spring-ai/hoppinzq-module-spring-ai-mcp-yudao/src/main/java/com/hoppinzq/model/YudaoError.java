package com.hoppinzq.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YudaoError {

    public YudaoErrorDetails error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class YudaoErrorDetails {
        String message;
        String type;
        String param;
        String code;
    }
}
