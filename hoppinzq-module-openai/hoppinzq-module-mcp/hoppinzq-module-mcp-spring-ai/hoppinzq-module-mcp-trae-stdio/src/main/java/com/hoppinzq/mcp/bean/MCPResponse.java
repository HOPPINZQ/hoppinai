package com.hoppinzq.mcp.bean;

public record MCPResponse(int code, String data, String msg) {
    public static MCPResponse success(String data) {
        return new MCPResponse(200, data, "success");
    }

    public static MCPResponse fail(String message) {
        return new MCPResponse(500, null, message);
    }
}
