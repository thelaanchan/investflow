package com.investflow.ai.validator;

import com.investflow.ai.exception.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SqlSafetyValidatorTest {

    private SqlSafetyValidator validator;

    @BeforeEach
    void setUp() {
        validator = new SqlSafetyValidator();
    }

    @Test
    void validateSafeSelect_ShouldPassForLegitimateSelect() {
        String safeSql = "SELECT symbol, units, invested_amount FROM investments WHERE user_id = 2 AND status = 'ACTIVE'";
        assertDoesNotThrow(() -> validator.validateSafeSelect(safeSql, 2L));
    }

    @Test
    void validateSafeSelect_ShouldRejectDropTable() {
        String dangerousSql = "DROP TABLE users";
        assertThrows(BadRequestException.class, () -> validator.validateSafeSelect(dangerousSql, 2L));
    }

    @Test
    void validateSafeSelect_ShouldRejectInsertOrUpdate() {
        String updateSql = "UPDATE investments SET units = 1000 WHERE user_id = 2";
        assertThrows(BadRequestException.class, () -> validator.validateSafeSelect(updateSql, 2L));

        String insertSql = "INSERT INTO users (email) VALUES ('hacker@test.com')";
        assertThrows(BadRequestException.class, () -> validator.validateSafeSelect(insertSql, 2L));
    }

    @Test
    void validateSafeSelect_ShouldRejectSemicolonChaining() {
        String chainedSql = "SELECT * FROM investments WHERE user_id = 2; DROP TABLE holdings";
        assertThrows(BadRequestException.class, () -> validator.validateSafeSelect(chainedSql, 2L));
    }

    @Test
    void validateSafeSelect_ShouldRejectSqlComments() {
        String commentSql = "SELECT * FROM investments WHERE user_id = 2 -- bypass check";
        assertThrows(BadRequestException.class, () -> validator.validateSafeSelect(commentSql, 2L));
    }

    @Test
    void validateSafeSelect_ShouldRejectCrossTenantQuery() {
        String crossTenantSql = "SELECT * FROM investments WHERE portfolio_id = 1";
        assertThrows(BadRequestException.class, () -> validator.validateSafeSelect(crossTenantSql, 2L));
    }
}
