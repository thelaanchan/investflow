package com.investflow.investment.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "investment_id", nullable = false)
    @JsonIgnore
    private Investment investment;

    @Column(name = "portfolio_id", nullable = false)
    private Long portfolioId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 30)
    private String type; // BUY, SELL, DIVIDEND

    @Column(nullable = false, precision = 18, scale = 4)
    private BigDecimal units;

    @Column(name = "price_per_unit", nullable = false, precision = 18, scale = 4)
    private BigDecimal pricePerUnit;

    @Column(name = "total_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "transaction_date", nullable = false)
    @Builder.Default
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(nullable = false, length = 30)
    @Builder.Default
    private String status = "COMPLETED"; // COMPLETED, PENDING, FAILED

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
