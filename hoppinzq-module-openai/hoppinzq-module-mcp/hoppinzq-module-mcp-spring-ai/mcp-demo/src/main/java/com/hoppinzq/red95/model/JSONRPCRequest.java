package com.hoppinzq.red95.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSONRPCRequest represents a JSON-RPC request object base structure
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JSONRPCRequest {
    @JsonProperty("id")
    Object id;
    @JsonProperty("jsonrpc")
    String jsonrpc;
    @JsonProperty("method")
    String method;
    @JsonProperty("params")
    Object params;

}
