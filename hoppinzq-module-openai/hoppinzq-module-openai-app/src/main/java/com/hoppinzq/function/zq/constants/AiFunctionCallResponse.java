package com.hoppinzq.function.zq.constants;

import lombok.Data;

@Data
public class AiFunctionCallResponse {
    private Boolean success;
    private String msg;

    public void fail(String reason) {
        this.success = false;
        this.msg = reason;
    }

    public void success(String msg) {
        this.success = true;
        this.msg = msg;
    }

}

