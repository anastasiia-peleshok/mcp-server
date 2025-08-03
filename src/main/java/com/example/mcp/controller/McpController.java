package com.example.mcp.controller;

import com.example.mcp.dto.mcp.McpRequest;
import com.example.mcp.dto.mcp.McpResponse;
import com.example.mcp.dto.mcp.McpTool;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

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
//    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<McpResponse> streamMcpRequest(@RequestBody McpRequest request) {
//        return Flux.create(sink -> {
//            new Thread(() -> {
//                try {
//                    log.info("Received MCP stream request: {}", request.getMethod());
//
//                    if ("tools/list".equals(request.getMethod())) {
//                        List<McpTool> availableTools = getAvailableTools();
//                        for (McpTool tool : availableTools) {
//                            McpResponse partialResponse = new McpResponse();
//                            partialResponse.setId(request.getId());
//                            partialResponse.setResult(Map.of("tool", tool));
//                            sink.next(partialResponse);
//
//                            //Thread.sleep(500);
//                        }
//
//                    } else if ("tools/call".equals(request.getMethod())) {
//                        for (int i = 1; i <= 3; i++) {
//                            McpResponse partialResponse = new McpResponse();
//                            partialResponse.setId(request.getId());
//                            partialResponse.setResult(Map.of("progress", "Step " + i + " of 3"));
//                            sink.next(partialResponse);
//
//                            Thread.sleep(1000);
//                        }
//
//                        McpResponse finalResponse = new McpResponse();
//                        finalResponse.setId(request.getId());
//                        finalResponse.setResult(Map.of("status", "completed"));
//                        sink.next(finalResponse);
//
//                    } else {
//                        McpResponse errorResponse = new McpResponse();
//                        McpResponse.McpError error = new McpResponse.McpError();
//                        error.setCode(-32601);
//                        error.setMessage("Method not found: " + request.getMethod());
//                        errorResponse.setError(error);
//                        sink.next(errorResponse);
//                    }
//
//                    sink.complete();
//                } catch (Exception e) {
//                    log.error("Error in MCP stream", e);
//                    McpResponse errorResponse = new McpResponse();
//                    McpResponse.McpError error = new McpResponse.McpError();
//                    error.setCode(-32603);
//                    error.setMessage("Internal error: " + e.getMessage());
//                    errorResponse.setError(error);
//                    sink.next(errorResponse);
//                    sink.complete();
//                }
//            }).start();
//        });
//    }

//
    @PostMapping(value = "/stream", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<McpResponse> streamMcpRequest(@RequestBody McpRequest request) {
        return Flux.create(sink -> {
            try {
                log.info("Received MCP stream request: {}", request.getMethod());

                McpResponse response = new McpResponse();
                response.setId(request.getId());
                switch (request.getMethod()) {
                    case "tools/list" -> response.setResult(Map.of("tools", getAvailableTools()));
                    case "tools/call" -> handleToolCall(request, response);
                    default -> {
                        McpResponse.McpError error = new McpResponse.McpError();
                        error.setCode(-32601);
                        error.setMessage("Method not found: " + request.getMethod());
                        response.setError(error);
                    }
                }

                sink.next(response);
                sink.complete();

            } catch (Exception e) {
                log.error("Error in MCP stream", e);
                McpResponse.McpError error = new McpResponse.McpError();
                error.setCode(-32603);
                error.setMessage("Internal error: " + e.getMessage());
                McpResponse errorResponse = new McpResponse();
                errorResponse.setError(error);
                sink.next(errorResponse);
                sink.error(e);
            }
        });
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
        tool.getToolDefinition().inputSchema();
        String inputSchema = tool.getToolDefinition().inputSchema();
        try {
            Map<String, Object> parsedSchema = objectMapper.readValue(inputSchema, Map.class);
            parsedSchema.remove("$schema");
            mcpTool.setInputSchema(parsedSchema);
        } catch (Exception e) {
            log.warn("Failed to parse input schema for tool {}", mcpTool.getName(), e);
            mcpTool.setInputSchema(Map.of("error", "invalid schema"));
        }

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

    @GetMapping("/tools/list")
    public ResponseEntity<Map<String, List<McpTool>>> getTools() {
        return ResponseEntity.ok(Map.of("tools", getAvailableTools()));
    }
}
