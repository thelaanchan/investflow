package com.investflow.portfolio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "holdings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Holding {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id", nullable = false)
    @JsonIgnore
    private Portfolio portfolio;

    @Column(name = "asset_symbol", nullable = false, length = 50)
    private String assetSymbol;

    @Column(name = "asset_name", nullable = false, length = 150)
    private String assetName;

    @Column(name = "asset_type", nullable = false, length = 50)
    @Builder.Default
    private String assetType = "EQUITY"; // EQUITY, MUTUAL_FUND, BOND, CRYPTO

    @Column(nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "average_buy_price", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal averageBuyPrice = BigDecimal.ZERO;

    @Column(name = "current_price", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal currentPrice = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BigDecimal getTotalInvested() {
        return quantity.multiply(averageBuyPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getCurrentValue() {
        return quantity.multiply(currentPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getProfitOrLoss() {
        return getCurrentValue().subtract(getTotalInvested()).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getReturnsPercentage() {
        BigDecimal totalInvested = getTotalInvested();
        if (totalInvested.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitOrLoss()
                .multiply(BigDecimal.valueOf(100))
                .divide(totalInvested, 2, RoundingMode.HALF_UP);
    }
}
