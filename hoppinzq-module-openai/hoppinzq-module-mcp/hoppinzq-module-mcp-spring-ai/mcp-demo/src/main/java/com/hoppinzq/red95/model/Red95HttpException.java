package com.hoppinzq.red95.model;

import lombok.Data;

@Data
public class Red95HttpException extends RuntimeException {
    public final int statusCode;
    public final String code;
    public final String param;
    public final String type;

    public Red95HttpException(Red95Error error, Exception parent, int statusCode) {
        super(error.error.message, parent);
        this.statusCode = statusCode;
        this.code = error.error.code;
        this.param = error.error.param;
        this.type = error.error.type;
    }
}
