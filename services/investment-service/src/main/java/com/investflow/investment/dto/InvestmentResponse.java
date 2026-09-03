package com.investflow.investment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentResponse {
    private Long id;
    private Long portfolioId;
    private Long userId;
    private String symbol;
    private String name;
    private String assetType;
    private BigDecimal units;
    private BigDecimal investedAmount;
    private BigDecimal currentNavOrPrice;
    private BigDecimal currentValue;
    private BigDecimal profitOrLoss;
    private BigDecimal returnsPercentage;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
