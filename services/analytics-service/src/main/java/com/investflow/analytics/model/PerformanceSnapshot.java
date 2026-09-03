package com.investflow.analytics.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "snapshot_date", nullable = false)
    private LocalDate snapshotDate;

    @Column(name = "total_invested", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalInvested;

    @Column(name = "current_value", nullable = false, precision = 18, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "total_profit_loss", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalProfitLoss;

    @Column(name = "returns_percentage", nullable = false, precision = 18, scale = 2)
    private BigDecimal returnsPercentage;

    @Column(name = "xirr_rate", precision = 18, scale = 4)
    private BigDecimal xirrRate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
