package com.investflow.analytics.config;

import com.investflow.analytics.model.PerformanceSnapshot;
import com.investflow.analytics.repository.PerformanceSnapshotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PerformanceSnapshotRepository snapshotRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (snapshotRepository.count() == 0) {
            log.info("Seeding historical performance snapshots for portfolio 1...");

            LocalDate now = LocalDate.now();
            List<PerformanceSnapshot> snapshots = List.of(
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now.minusMonths(6)).totalInvested(new BigDecimal("18000.00")).currentValue(new BigDecimal("18500.00")).totalProfitLoss(new BigDecimal("500.00")).returnsPercentage(new BigDecimal("2.78")).xirrRate(new BigDecimal("5.60")).build(),
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now.minusMonths(5)).totalInvested(new BigDecimal("22000.00")).currentValue(new BigDecimal("23100.00")).totalProfitLoss(new BigDecimal("1100.00")).returnsPercentage(new BigDecimal("5.00")).xirrRate(new BigDecimal("8.20")).build(),
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now.minusMonths(4)).totalInvested(new BigDecimal("25000.00")).currentValue(new BigDecimal("27200.00")).totalProfitLoss(new BigDecimal("2200.00")).returnsPercentage(new BigDecimal("8.80")).xirrRate(new BigDecimal("12.40")).build(),
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now.minusMonths(3)).totalInvested(new BigDecimal("27000.00")).currentValue(new BigDecimal("30100.00")).totalProfitLoss(new BigDecimal("3100.00")).returnsPercentage(new BigDecimal("11.48")).xirrRate(new BigDecimal("14.80")).build(),
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now.minusMonths(2)).totalInvested(new BigDecimal("28500.00")).currentValue(new BigDecimal("32900.00")).totalProfitLoss(new BigDecimal("4400.00")).returnsPercentage(new BigDecimal("15.44")).xirrRate(new BigDecimal("17.50")).build(),
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now.minusMonths(1)).totalInvested(new BigDecimal("29300.00")).currentValue(new BigDecimal("34500.00")).totalProfitLoss(new BigDecimal("5200.00")).returnsPercentage(new BigDecimal("17.75")).xirrRate(new BigDecimal("19.20")).build(),
                    PerformanceSnapshot.builder().portfolioId(1L).userId(2L).snapshotDate(now).totalInvested(new BigDecimal("29337.50")).currentValue(new BigDecimal("35859.00")).totalProfitLoss(new BigDecimal("6521.50")).returnsPercentage(new BigDecimal("22.23")).xirrRate(new BigDecimal("22.85")).build()
            );

            snapshotRepository.saveAll(snapshots);
            log.info("Historical performance snapshots seeded successfully.");
        }
    }
}
