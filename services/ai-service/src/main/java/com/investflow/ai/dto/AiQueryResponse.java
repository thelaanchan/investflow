package com.investflow.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiQueryResponse {
    private String question;
    private String generatedSql;
    private String explanation;
    private List<String> columns;
    private List<Map<String, Object>> rows;
    private long executionTimeMs;
}
