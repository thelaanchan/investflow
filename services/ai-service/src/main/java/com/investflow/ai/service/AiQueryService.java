package com.investflow.ai.service;

import com.investflow.ai.dto.AiQueryRequest;
import com.investflow.ai.dto.AiQueryResponse;
import com.investflow.ai.validator.SqlSafetyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiQueryService {

    private final JdbcTemplate jdbcTemplate;
    private final SqlSafetyValidator validator;

    public AiQueryResponse processNaturalLanguageQuery(Long userId, AiQueryRequest request) {
        long startTime = System.currentTimeMillis();
        String question = request.getQuestion().trim();
        String lowerQuestion = question.toLowerCase();

        String generatedSql;
        String explanation;

        // Semantic Natural-Language-to-SQL Intent Analyzer
        if (lowerQuestion.contains("total") && (lowerQuestion.contains("invest") || lowerQuestion.contains("worth") || lowerQuestion.contains("value"))) {
            generatedSql = "SELECT COUNT(*) as total_holdings, SUM(invested_amount) as total_invested, " +
                    "SUM(units * current_nav_or_price) as current_portfolio_value, " +
                    "SUM(units * current_nav_or_price) - SUM(invested_amount) as total_gain " +
                    "FROM investments WHERE user_id = " + userId + " AND status = 'ACTIVE'";
            explanation = "Calculated the total aggregated capital invested, current market valuation, and cumulative unrealized gains across all active holdings.";
        } else if (lowerQuestion.contains("highest") || lowerQuestion.contains("best") || lowerQuestion.contains("top")) {
            generatedSql = "SELECT TOP 5 symbol, name, units, invested_amount, " +
                    "(units * current_nav_or_price) as current_value, " +
                    "((units * current_nav_or_price) - invested_amount) as net_profit " +
                    "FROM investments WHERE user_id = " + userId + " AND status = 'ACTIVE' " +
                    "ORDER BY ((units * current_nav_or_price) - invested_amount) DESC";
            explanation = "Identified your top-performing investments ranked by absolute profit generated.";
        } else if (lowerQuestion.contains("sip") || lowerQuestion.contains("recurring") || lowerQuestion.contains("systematic")) {
            generatedSql = "SELECT symbol, name, frequency, installment_amount, day_of_month, " +
                    "next_execution_date, total_invested, status " +
                    "FROM sips WHERE user_id = " + userId + " ORDER BY next_execution_date ASC";
            explanation = "Retrieved all active and scheduled Systematic Investment Plans (SIPs) ordered by next installment execution date.";
        } else if (lowerQuestion.contains("allocation") || lowerQuestion.contains("breakdown") || lowerQuestion.contains("asset")) {
            generatedSql = "SELECT asset_type, COUNT(*) as asset_count, " +
                    "SUM(invested_amount) as invested_in_asset, " +
                    "SUM(units * current_nav_or_price) as current_asset_value " +
                    "FROM investments WHERE user_id = " + userId + " AND status = 'ACTIVE' " +
                    "GROUP BY asset_type";
            explanation = "Grouped portfolio exposure by asset class (Equity, Mutual Funds, Bonds) detailing total allocation amounts.";
        } else if (lowerQuestion.contains("transaction") || lowerQuestion.contains("history") || lowerQuestion.contains("trade")) {
            generatedSql = "SELECT TOP 10 t.type, t.units, t.price_per_unit, t.total_amount, " +
                    "t.transaction_date, t.status, i.symbol " +
                    "FROM transactions t JOIN investments i ON t.investment_id = i.id " +
                    "WHERE t.user_id = " + userId + " ORDER BY t.transaction_date DESC";
            explanation = "Retrieved the 10 most recent trade transactions executed on your account.";
        } else {
            // Default listing of holdings
            generatedSql = "SELECT symbol, name, asset_type, units, invested_amount, " +
                    "current_nav_or_price, (units * current_nav_or_price) as current_value " +
                    "FROM investments WHERE user_id = " + userId + " AND status = 'ACTIVE'";
            explanation = "Retrieved all current active investment positions with latest price marks.";
        }

        // Strict AST/Regex Safety Validation
        validator.validateSafeSelect(generatedSql, userId);

        // Execute query safely with 5-second timeout
        jdbcTemplate.setQueryTimeout(5);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(generatedSql);

        List<String> columns = rows.isEmpty() ? List.of() : new ArrayList<>(rows.get(0).keySet());
        long executionTime = System.currentTimeMillis() - startTime;

        log.info("AI Query executed in {}ms for user {}: {}", executionTime, userId, question);

        return AiQueryResponse.builder()
                .question(question)
                .generatedSql(generatedSql)
                .explanation(explanation)
                .columns(columns)
                .rows(rows)
                .executionTimeMs(executionTime)
                .build();
    }

    public List<String> getSampleQuestions() {
        return List.of(
                "What is my total investment and portfolio value?",
                "Which investment has the highest return?",
                "What is my asset allocation breakdown?",
                "Show my active SIP investments.",
                "List my recent trade transactions.",
                "What are my current active stock positions?"
        );
    }
}
