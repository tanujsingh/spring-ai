package com.llm.dto;

import jakarta.validation.constraints.NotBlank;

public record UserInput(@NotBlank(message = "Prompt is required") String prompt,
                        String context
//                        ,ChatOptions chatOptions
                        ) {
}


