package com.investflow.investment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SipResponse {
    private Long id;
    private Long portfolioId;
    private Long userId;
    private String symbol;
    private String name;
    private String frequency;
    private BigDecimal installmentAmount;
    private Integer dayOfMonth;
    private LocalDate nextExecutionDate;
    private String status;
    private BigDecimal totalInvested;
    private LocalDateTime createdAt;
}
