package com.investflow.ai.service;

import com.investflow.ai.dto.AiQueryRequest;
import com.investflow.ai.dto.AiQueryResponse;
import com.investflow.ai.validator.SqlSafetyValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AiQueryServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Spy
    private SqlSafetyValidator validator = new SqlSafetyValidator();

    @InjectMocks
    private AiQueryService aiQueryService;

    @Test
    void processNaturalLanguageQuery_ShouldGenerateAndExecuteSql() {
        AiQueryRequest request = AiQueryRequest.builder()
                .question("What is my total investment?")
                .build();

        List<Map<String, Object>> mockRows = List.of(
                Map.of("total_holdings", 4, "total_invested", 29337.50, "current_portfolio_value", 35859.00)
        );

        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockRows);

        AiQueryResponse response = aiQueryService.processNaturalLanguageQuery(2L, request);

        assertNotNull(response);
        assertTrue(response.getGeneratedSql().contains("SELECT"));
        assertTrue(response.getGeneratedSql().contains("user_id = 2"));
        assertFalse(response.getRows().isEmpty());
        assertNotNull(response.getExplanation());
    }

    @Test
    void getSampleQuestions_ShouldReturnValidList() {
        List<String> samples = aiQueryService.getSampleQuestions();
        assertNotNull(samples);
        assertFalse(samples.isEmpty());
    }
}
