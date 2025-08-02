package com.example.mcp.dto.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class McpResponse {
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    private String id;
    private Object result;
    private McpError error;
    
    @Data
    public static class McpError {
        private int code;
        private String message;
    }
} 