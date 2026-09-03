package com.investflow.investment.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sip {

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

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String frequency = "MONTHLY"; // MONTHLY, WEEKLY

    @Column(name = "installment_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal installmentAmount;

    @Column(name = "day_of_month", nullable = false)
    @Builder.Default
    private Integer dayOfMonth = 1;

    @Column(name = "next_execution_date", nullable = false)
    private LocalDate nextExecutionDate;

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "ACTIVE"; // ACTIVE, PAUSED, CANCELLED

    @Column(name = "total_invested", nullable = false, precision = 18, scale = 2)
    @Builder.Default
    private BigDecimal totalInvested = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
