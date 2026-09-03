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
public class TransactionResponse {
    private Long id;
    private Long investmentId;
    private Long portfolioId;
    private Long userId;
    private String type;
    private BigDecimal units;
    private BigDecimal pricePerUnit;
    private BigDecimal totalAmount;
    private LocalDateTime transactionDate;
    private String status;
}
