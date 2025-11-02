package com.hoppinzq.red95.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * JSONRPCError represents a JSON-RPC error object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class JSONRPCError {
    @JsonProperty("code")
    int code;
    @JsonProperty("message")
    String message;
    @JsonProperty("data")
    Object data;

}
