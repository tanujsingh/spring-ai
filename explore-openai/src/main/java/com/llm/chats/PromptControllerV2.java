package com.llm.chats;

import com.llm.dto.UserInputEnhanced;
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

/**
 * Example controller showing best practices for using .st templates
 * with language parameters and other dynamic values
 */
@RestController
@RequestMapping("/api/v2")
public class PromptControllerV2 {

    private static final Logger log = LoggerFactory.getLogger(PromptControllerV2.class);
    private final ChatClient chatClient;

    @Value("classpath:/prompt-templates/coding-assistant.st")
    private Resource codingAssistantTemplate;

    @Value("classpath:/prompt-templates/example-dynamic.st")
    private Resource dynamicTemplate;

    public PromptControllerV2(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    /**
     * Example 1: Using language parameter from the request body *:
     * {
     *   "prompt": "How do I use decorators?",
     *   "language": "Python",
     *   "taskType": "Syntax Help",
     *   "difficultyLevel": "beginner",
     *   "framework": "Django"
     * }
     */
    @PostMapping("/prompts")
    public String processPromptWithLanguage(@RequestBody UserInputEnhanced input) {
        log.info("Processing prompt with language: {}", input.language());

        var systemPromptTemplate = new SystemPromptTemplate(codingAssistantTemplate);

        // Build parameters from the request
        Map<String, Object> params = Map.of(
            "language", input.getLanguageOrDefault()
        );

        var systemMessage = systemPromptTemplate.createMessage(params);
        var userMessage = new UserMessage(input.prompt());
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var responseSpec = chatClient.prompt(prompt).call();
        return responseSpec.content();
    }

    /**
     * Example 2: Using multiple dynamic parameters
     * Request body:
     * {
     *   "prompt": "How do I implement validation?",
     *   "language": "TypeScript",
     *   "taskType": "Best Practices",
     *   "difficultyLevel": "advanced",
     *   "framework": "NestJS"
     * }
     */
    @PostMapping("/prompts/advanced")
    public String processAdvancedPrompt(@RequestBody UserInputEnhanced input) {
        log.info("Processing advanced prompt for language: {}", input.language());

        var systemPromptTemplate = new SystemPromptTemplate(dynamicTemplate);

        // Build rich parameters from the request
        Map<String, Object> params = Map.of(
            "language", input.getLanguageOrDefault(),
            "yearsOfExperience", "10+",  // Can also come from input
            "taskType", input.getTaskTypeOrDefault(),
            "difficulty", input.getDifficultyLevelOrDefault(),
            "focusAreas", input.framework() != null ? input.framework() : "Core concepts"
        );

        var systemMessage = systemPromptTemplate.createMessage(params);
        var userMessage = new UserMessage(input.prompt());
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var responseSpec = chatClient.prompt(prompt).call();
        return responseSpec.content();
    }

    /**
     * Example 3: Language as path variable (URL parameter)
     * Usage: POST /api/v2/prompts/language/Python
     */
    @PostMapping("/prompts/language/{language}")
    public String processPromptByLanguage(
            @PathVariable String language,
            @RequestBody String userPrompt) {
        log.info("Processing prompt for language: {}", language);

        var systemPromptTemplate = new SystemPromptTemplate(codingAssistantTemplate);

        Map<String, Object> params = Map.of(
            "language", language
        );

        var systemMessage = systemPromptTemplate.createMessage(params);
        var userMessage = new UserMessage(userPrompt);
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var responseSpec = chatClient.prompt(prompt).call();
        return responseSpec.content();
    }

    /**
     * Example 4: Language as query parameter
     * Usage: POST /api/v2/prompts?language=Go&difficulty=expert
     */
    @PostMapping("/prompts/with-params")
    public String processPromptWithParams(
            @RequestParam(defaultValue = "Java") String language,
            @RequestParam(defaultValue = "intermediate") String difficulty,
            @RequestBody String userPrompt) {
        log.info("Processing prompt - Language: {}, Difficulty: {}", language, difficulty);

        var systemPromptTemplate = new SystemPromptTemplate(codingAssistantTemplate);

        Map<String, Object> params = Map.of(
            "language", language
        );

        var systemMessage = systemPromptTemplate.createMessage(params);
        var userMessage = new UserMessage(userPrompt);
        var prompt = new Prompt(List.of(systemMessage, userMessage));

        var responseSpec = chatClient.prompt(prompt).call();
        return responseSpec.content();
    }
}

