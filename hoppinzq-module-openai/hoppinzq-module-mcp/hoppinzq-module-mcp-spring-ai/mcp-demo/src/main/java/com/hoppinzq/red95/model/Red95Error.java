package com.hoppinzq.red95.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Red95Error {

    public Red95ErrorDetails error;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Red95ErrorDetails {
        String message;
        String type;
        String param;
        String code;
    }
}
