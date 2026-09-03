package com.investflow.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioAnalyticsResponse {
    private Long portfolioId;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal returnsPercentage;
    private BigDecimal xirrPercentage;
    private int holdingsCount;
    private Map<String, BigDecimal> assetAllocation;
    private String riskLevel; // LOW, MODERATE, HIGH
}
