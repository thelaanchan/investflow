package com.investflow.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceMetricsResponse {
    private Long portfolioId;
    private BigDecimal currentPortfolioValue;
    private BigDecimal benchmarkReturnPercentage; // e.g. S&P 500 comparison (12.4%)
    private BigDecimal alpha; // Excess return over benchmark
    private BigDecimal beta; // Volatility metric
    private BigDecimal sharpeRatio;
    private List<SnapshotPoint> timeline;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SnapshotPoint {
        private LocalDate date;
        private BigDecimal invested;
        private BigDecimal value;
        private BigDecimal returns;
    }
}
