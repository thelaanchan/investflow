package com.investflow.ai.controller;

import com.investflow.ai.dto.AiQueryRequest;
import com.investflow.ai.dto.AiQueryResponse;
import com.investflow.ai.dto.ApiResponse;
import com.investflow.ai.service.AiQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Financial Assistant", description = "Natural-Language-to-SQL financial querying engine with strict security guardrails")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiQueryService aiQueryService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        return userId != null ? userId : 2L;
    }

    @PostMapping("/query")
    @Operation(summary = "Ask a natural language financial question (converts to safe SQL and executes)")
    public ResponseEntity<ApiResponse<AiQueryResponse>> askQuestion(
            HttpServletRequest request,
            @Valid @RequestBody AiQueryRequest body) {
        AiQueryResponse response = aiQueryService.processNaturalLanguageQuery(getUserId(request), body);
        return ResponseEntity.ok(ApiResponse.success(response, "Query executed successfully"));
    }

    @GetMapping("/sample-questions")
    @Operation(summary = "Get list of suggested natural language queries")
    public ResponseEntity<ApiResponse<List<String>>> getSampleQuestions() {
        List<String> samples = aiQueryService.getSampleQuestions();
        return ResponseEntity.ok(ApiResponse.success(samples, "Sample questions retrieved"));
    }
}
