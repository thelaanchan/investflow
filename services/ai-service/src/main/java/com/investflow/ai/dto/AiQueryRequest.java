package com.investflow.ai.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryRequest {

    @NotBlank(message = "Question cannot be blank")
    private String question;

    private Long portfolioId;
}
