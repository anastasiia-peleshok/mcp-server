package com.example.mcp.dto.mcp;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class McpRequest {
    @JsonProperty("jsonrpc")
    private String jsonrpc = "2.0";
    
    private String id;
    private String method;
    private McpParams params;
    
    @Data
    public static class McpParams {
        private String name;
        private Object arguments;
    }
} 