package com.example.mcp;

import com.example.mcp.controller.MutationController;
import com.example.mcp.controller.QueryController;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class McpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

	@Bean
	public ToolCallbackProvider tools(MutationController mutationController, QueryController queryController) {
		return  MethodToolCallbackProvider.builder()
				.toolObjects(mutationController, queryController)
				.build();
	}
}
