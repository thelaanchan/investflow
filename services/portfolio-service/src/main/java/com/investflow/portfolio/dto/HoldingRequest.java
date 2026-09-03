package com.investflow.portfolio.dto;

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
public class HoldingRequest {

    @NotBlank(message = "Asset symbol is required")
    private String assetSymbol;

    @NotBlank(message = "Asset name is required")
    private String assetName;

    @Builder.Default
    private String assetType = "EQUITY"; // EQUITY, MUTUAL_FUND, BOND, CRYPTO

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001", message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Average buy price is required")
    @DecimalMin(value = "0.01", message = "Buy price must be greater than 0")
    private BigDecimal averageBuyPrice;

    @NotNull(message = "Current price is required")
    @DecimalMin(value = "0.01", message = "Current price must be greater than 0")
    private BigDecimal currentPrice;
}
