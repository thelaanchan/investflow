package com.investflow.investment.dto;

import jakarta.validation.constraints.DecimalMin;
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
public class TradeRequest {

    @NotNull(message = "Quantity/Units is required")
    @DecimalMin(value = "0.0001", message = "Units must be greater than 0")
    private BigDecimal units;

    @NotNull(message = "Execution price is required")
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
}
