package com.investflow.ai.validator;

import com.investflow.ai.exception.BadRequestException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class SqlSafetyValidator {

    private static final Pattern MULTIPLE_STATEMENTS = Pattern.compile(";");
    private static final Pattern COMMENT_PATTERN = Pattern.compile("(--|/\\*|\\*/)");
    private static final List<String> FORBIDDEN_KEYWORDS = List.of(
            "INSERT", "UPDATE", "DELETE", "DROP", "ALTER", "TRUNCATE", "EXEC", "EXECUTE",
            "CREATE", "SHUTDOWN", "GRANT", "REVOKE", "INTO", "XP_", "SP_", "MERGE", "CALL"
    );

    public void validateSafeSelect(String sql, Long userId) {
        if (sql == null || sql.trim().isEmpty()) {
            throw new BadRequestException("SQL query is empty");
        }

        String normalized = sql.trim().toUpperCase();

        // 1. Must strictly start with SELECT or WITH (for CTEs)
        if (!normalized.startsWith("SELECT") && !normalized.startsWith("WITH")) {
            throw new BadRequestException("Only read-only SELECT queries are permitted by AI safety policy");
        }

        // 2. Reject statement chaining (semicolons)
        if (MULTIPLE_STATEMENTS.matcher(sql).find()) {
            throw new BadRequestException("Query chaining is prohibited");
        }

        // 3. Reject comments
        if (COMMENT_PATTERN.matcher(sql).find()) {
            throw new BadRequestException("SQL comments are prohibited");
        }

        // 4. Reject all DDL, DML, or procedure keywords
        for (String kw : FORBIDDEN_KEYWORDS) {
            // Word boundary regex check
            Pattern kwPattern = Pattern.compile("\\b" + kw + "\\b", Pattern.CASE_INSENSITIVE);
            if (kwPattern.matcher(sql).find()) {
                throw new BadRequestException("Forbidden SQL operation detected: " + kw);
            }
        }

        // 5. Enforce tenant isolation parameter presence
        if (userId != null && !sql.contains(userId.toString()) && !normalized.contains("USER_ID")) {
            throw new BadRequestException("Query violated multi-tenant isolation: missing user_id scope");
        }
    }
}
