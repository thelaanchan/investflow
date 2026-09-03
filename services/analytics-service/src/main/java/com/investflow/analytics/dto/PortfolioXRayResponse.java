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
public class PortfolioXRayResponse {
    private Long portfolioId;
    private String overallRiskGrade; // Conservative, Moderate, Aggressive
    private Map<String, BigDecimal> sectorExposure; // Technology: 45%, Financials: 20%, Healthcare: 15%
    private Map<String, BigDecimal> marketCapDistribution; // Large Cap: 70%, Mid Cap: 20%, Small Cap: 10%
    private Map<String, BigDecimal> geographicalAllocation; // North America: 85%, Global: 15%
    private BigDecimal portfolioDiversificationScore; // 0 - 100
}
