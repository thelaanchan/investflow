package com.investflow.investment.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SipRequest {

    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotBlank(message = "Name is required")
    private String name;

    @Builder.Default
    private String frequency = "MONTHLY"; // MONTHLY, WEEKLY

    @NotNull(message = "Installment amount is required")
    @DecimalMin(value = "1.00", message = "Installment amount must be at least 1.00")
    private BigDecimal installmentAmount;

    @Min(value = 1, message = "Day of month must be between 1 and 28")
    @Max(value = 28, message = "Day of month must be between 1 and 28")
    @Builder.Default
    private Integer dayOfMonth = 1;
}
