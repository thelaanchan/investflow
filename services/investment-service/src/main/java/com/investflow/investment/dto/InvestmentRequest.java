package com.investflow.investment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvestmentRequest {

    @NotNull(message = "Portfolio ID is required")
    private Long portfolioId;

    @NotBlank(message = "Symbol is required")
    private String symbol;

    @NotBlank(message = "Name is required")
    private String name;

    @Builder.Default
    private String assetType = "EQUITY"; // EQUITY, MUTUAL_FUND, BOND, CRYPTO

    @NotNull(message = "Initial units required")
    @DecimalMin(value = "0.0001", message = "Units must be positive")
    private BigDecimal units;

    @NotNull(message = "Price per unit is required")
    @DecimalMin(value = "0.01", message = "Price must be positive")
    private BigDecimal pricePerUnit;
}
