package com.investflow.portfolio.dto;

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
public class AssetAllocationResponse {
    private Long portfolioId;
    private BigDecimal totalPortfolioValue;
    private Map<String, BigDecimal> allocationByAssetType; // e.g. EQUITY: 60.50, MUTUAL_FUND: 25.00
    private Map<String, BigDecimal> valueByAssetType;
}
