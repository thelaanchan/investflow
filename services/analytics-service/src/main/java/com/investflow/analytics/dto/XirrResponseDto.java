package com.investflow.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class XirrResponseDto {
    private BigDecimal xirr;
    private BigDecimal annualizedPercentage;
    private int iterations;
    private boolean converged;
}
