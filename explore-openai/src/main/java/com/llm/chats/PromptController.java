package com.llm.chats;

import com.llm.dto.UserInput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class PromptController {


    private static final Logger log = LoggerFactory.getLogger(PromptController.class);
    private final ChatClient chatClient;


    @Value("classpath:/prompt-templates/coding-assistant.st")
    private Resource systemText;

    @Value("classpath:/prompt-templates/multi-param-template.st")
    private Resource multiParamTemplate;


    public PromptController(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @PostMapping("/v1/prompts")
    public String prompts(@RequestBody UserInput userInput) {
        log.info("User Input: {}", userInput);

        // Method 1: Using SystemPromptTemplate to load and process .st file with parameters
        var systemPromptTemplate = new SystemPromptTemplate(systemText);

        // Create a map of parameters to pass to the template
        Map<String, Object> params = Map.of(
            "language", "Java"  // This will replace {language} in coding-assistant.st
        );

        // Process the template with the parameters
        var systemMessage = systemPromptTemplate.createMessage(params);

        // Create the user message from the input
        var userMessage = new UserMessage(userInput.prompt());

        // Create a prompt with both system and user messages
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        // Call the AI model
        var responseSpec = chatClient.prompt(prompt).call();

        return responseSpec.content();
    }

    /**
     * Advanced example: Using .st templates with multiple parameters
     * Request body example:
     * {
     *   "prompt": "How do I implement dependency injection?",
     *   "context": "Spring Framework"
     * }
     */
    @PostMapping("/v1/prompts/advanced")
    public String advancedPrompts(@RequestBody UserInput userInput) {
        log.info("Advanced User Input: {}", userInput);

        // Load the multi-parameter template
        var systemPromptTemplate = new SystemPromptTemplate(multiParamTemplate);

        // Create a map with multiple parameters to replace in the template
        Map<String, Object> params = Map.of(
            "projectType", "Spring Boot Application",
            "framework", "Spring AI",
            "version", "1.0.0",
            "task", "Generate code snippets",
            "language", "Java",
            "codeStandard", "Google Java Style",
            "docType", "JavaDoc comments",
            "userQuery", userInput.prompt()
        );

        // Process the template with all parameters
        var systemMessage = systemPromptTemplate.createMessage(params);
        var userMessage = new UserMessage(userInput.prompt());
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        // Call the AI model
        var responseSpec = chatClient.prompt(prompt).call();
        return responseSpec.content();
    }

    /**
     * Example with dynamic language parameter
     * You can pass the language from the request body or path variable
     */
    @PostMapping("/v1/prompts/by-language/{language}")
    public String promptsByLanguage(
            @PathVariable String language,
            @RequestBody UserInput userInput) {
        log.info("Prompts for language: {}", language);

        var systemPromptTemplate = new SystemPromptTemplate(systemText);

        // Dynamically set the language parameter based on the path variable
        Map<String, Object> params = Map.of(
            "language", language
        );

        var systemMessage = systemPromptTemplate.createMessage(params);
        var userMessage = new UserMessage(userInput.prompt());
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var responseSpec = chatClient.prompt(prompt).call();
        return responseSpec.content();
    }
}
