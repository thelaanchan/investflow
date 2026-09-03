package com.investflow.portfolio.dto;

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
public class HoldingResponse {
    private Long id;
    private Long portfolioId;
    private String assetSymbol;
    private String assetName;
    private String assetType;
    private BigDecimal quantity;
    private BigDecimal averageBuyPrice;
    private BigDecimal currentPrice;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal profitOrLoss;
    private BigDecimal returnsPercentage;
    private LocalDateTime updatedAt;
}
