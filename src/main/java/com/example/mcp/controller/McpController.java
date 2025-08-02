package com.example.mcp.controller;

import com.example.mcp.dto.mcp.McpRequest;
import com.example.mcp.dto.mcp.McpResponse;
import com.example.mcp.dto.mcp.McpTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/mcp")
@RequiredArgsConstructor
@Slf4j
public class McpController {

    private final ToolCallbackProvider toolCallbackProvider;
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/stream", consumes = "application/json", produces = "application/json")
    public ResponseEntity<McpResponse> handleMcpRequest(@RequestBody McpRequest request) {
        log.info("Received MCP request: {}", request.getMethod());
        
        McpResponse response = new McpResponse();
        response.setId(request.getId());
        
        try {
            switch (request.getMethod()) {
                case "tools/list":
                    response.setResult(Map.of("tools", getAvailableTools()));
                    break;
                    
                case "tools/call":
                    handleToolCall(request, response);
                    break;
                    
                default:
                    McpResponse.McpError error = new McpResponse.McpError();
                    error.setCode(-32601);
                    error.setMessage("Method not found: " + request.getMethod());
                    response.setError(error);
            }
        } catch (Exception e) {
            log.error("Error handling MCP request", e);
            McpResponse.McpError error = new McpResponse.McpError();
            error.setCode(-32603);
            error.setMessage("Internal error: " + e.getMessage());
            response.setError(error);
        }
        
        return ResponseEntity.ok(response);
    }

    private List<McpTool> getAvailableTools() {
        return Arrays.stream(toolCallbackProvider.getToolCallbacks())
                .map(this::convertToMcpTool)
                .toList();
    }

    private McpTool convertToMcpTool(ToolCallback tool) {
        McpTool mcpTool = new McpTool();
        mcpTool.setName(tool.getToolDefinition().name());
        mcpTool.setDescription(tool.getToolDefinition().description());
        mcpTool.setInputSchema(tool.getToolDefinition().inputSchema());
        return mcpTool;
    }

    private void handleToolCall(McpRequest request, McpResponse response) {
        String toolName = (String) request.getParams().getName();
        Object argumentsObj = request.getParams().getArguments();
        
        log.info("Calling tool: {} with arguments: {}", toolName, argumentsObj);
        
        try {
            Optional<ToolCallback> tool = Arrays.stream(toolCallbackProvider.getToolCallbacks())
                    .filter(t -> t.getToolDefinition().name().equals(toolName))
                    .findFirst();
            
            if (tool.isEmpty()) {
                throw new IllegalArgumentException("Unknown tool: " + toolName);
            }
            
            String argumentsJson;
            if (argumentsObj instanceof String) {
                argumentsJson = (String) argumentsObj;
            } else if (argumentsObj instanceof Map) {
                argumentsJson = objectMapper.writeValueAsString(argumentsObj);
            } else {
                throw new IllegalArgumentException("Invalid arguments format");
            }
            
            log.info("Calling tool with JSON arguments: {}", argumentsJson);
            Object result = tool.get().call(argumentsJson);
            response.setResult(result);
            
        } catch (Exception e) {
            log.error("Error calling tool: {}", toolName, e);
            McpResponse.McpError error = new McpResponse.McpError();
            error.setCode(-32603);
            error.setMessage("Tool execution error: " + e.getMessage());
            response.setError(error);
        }
    }

    @GetMapping("/tools")
    public ResponseEntity<Map<String, List<McpTool>>> getTools() {
        return ResponseEntity.ok(Map.of("tools", getAvailableTools()));
    }
}
