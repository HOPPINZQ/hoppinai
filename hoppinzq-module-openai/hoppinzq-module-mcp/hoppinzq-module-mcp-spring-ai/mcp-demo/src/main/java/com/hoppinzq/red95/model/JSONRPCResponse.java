package com.hoppinzq.red95.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JSONRPCResponse represents a JSON-RPC response object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JSONRPCResponse {
    @JsonProperty("id")
    Object id;
    @JsonProperty("jsonrpc")
    String jsonrpc;
    @JsonProperty("result")
    Object result;
    @JsonProperty("error")
    JSONRPCError error;

}
