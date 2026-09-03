package com.investflow.portfolio.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PortfolioResponse {
    private Long id;
    private Long userId;
    private String name;
    private String description;
    private String type;
    private BigDecimal totalInvested;
    private BigDecimal currentValue;
    private BigDecimal totalProfitLoss;
    private BigDecimal returnsPercentage;
    private int holdingsCount;
    private List<HoldingResponse> holdings;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
