package com.investflow.investment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "investments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Investment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 50)
    private String symbol;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "asset_type", nullable = false, length = 50)
    @Builder.Default
    private String assetType = "EQUITY";

    @Column(nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal units = BigDecimal.ZERO;

    @Column(name = "invested_amount", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal investedAmount = BigDecimal.ZERO;

    @Column(name = "current_nav_or_price", nullable = false, precision = 18, scale = 4)
    @Builder.Default
    private BigDecimal currentNavOrPrice = BigDecimal.ZERO;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, EXITED

    @OneToMany(mappedBy = "investment", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public BigDecimal getCurrentValue() {
        return units.multiply(currentNavOrPrice).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getProfitOrLoss() {
        return getCurrentValue().subtract(investedAmount).setScale(2, RoundingMode.HALF_UP);
    }

    public BigDecimal getReturnsPercentage() {
        if (investedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return getProfitOrLoss()
                .multiply(BigDecimal.valueOf(100))
                .divide(investedAmount, 2, RoundingMode.HALF_UP);
    }
}
