package com.hoppinzq.red95.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Red95Request {

    private String apiVersion;
    private String requestId;
    private String command;
    private Object params;
    private String language;

}

