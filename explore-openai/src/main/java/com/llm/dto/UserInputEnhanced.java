package com.llm.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Enhanced UserInput DTO with support for language and additional parameters
 * for .st template substitution
 */
public record UserInputEnhanced(
    @NotBlank(message = "Prompt is required") String prompt,
    String context,
    String language,           // For templating
    String taskType,           // For templating
    String difficultyLevel,    // For templating
    String framework           // For templating
) {

    /**
     * Get language with a default fallback
     */
    public String getLanguageOrDefault() {
        return language != null && !language.isBlank() ? language : "Java";
    }

    /**
     * Get task type with a default fallback
     */
    public String getTaskTypeOrDefault() {
        return taskType != null && !taskType.isBlank() ? taskType : "General Questions";
    }

    /**
     * Get difficulty level with a default fallback
     */
    public String getDifficultyLevelOrDefault() {
        return difficultyLevel != null && !difficultyLevel.isBlank() ? difficultyLevel : "intermediate";
    }
}

