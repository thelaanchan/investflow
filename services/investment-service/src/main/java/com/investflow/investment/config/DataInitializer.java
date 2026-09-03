package com.investflow.investment.config;

import com.investflow.investment.model.Investment;
import com.investflow.investment.model.Sip;
import com.investflow.investment.model.Transaction;
import com.investflow.investment.repository.InvestmentRepository;
import com.investflow.investment.repository.SipRepository;
import com.investflow.investment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final InvestmentRepository investmentRepository;
    private final TransactionRepository transactionRepository;
    private final SipRepository sipRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (investmentRepository.count() == 0) {
            log.info("Seeding demo investments, transactions, and SIP schedules...");

            Long portfolioId = 1L;
            Long userId = 2L;

            // 1. Apple Inc.
            Investment aapl = Investment.builder()
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .symbol("AAPL")
                    .name("Apple Inc.")
                    .assetType("EQUITY")
                    .units(new BigDecimal("25.0000"))
                    .investedAmount(new BigDecimal("4387.50"))
                    .currentNavOrPrice(new BigDecimal("228.4000"))
                    .status("ACTIVE")
                    .build();
            Investment savedAapl = investmentRepository.save(aapl);

            Transaction aaplTx1 = Transaction.builder()
                    .investment(savedAapl)
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .type("BUY")
                    .units(new BigDecimal("15.0000"))
                    .pricePerUnit(new BigDecimal("170.0000"))
                    .totalAmount(new BigDecimal("2550.00"))
                    .transactionDate(LocalDateTime.now().minusMonths(6))
                    .status("COMPLETED")
                    .build();

            Transaction aaplTx2 = Transaction.builder()
                    .investment(savedAapl)
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .type("BUY")
                    .units(new BigDecimal("10.0000"))
                    .pricePerUnit(new BigDecimal("183.7500"))
                    .totalAmount(new BigDecimal("1837.50"))
                    .transactionDate(LocalDateTime.now().minusMonths(3))
                    .status("COMPLETED")
                    .build();

            transactionRepository.saveAll(List.of(aaplTx1, aaplTx2));

            // 2. Microsoft Corporation
            Investment msft = Investment.builder()
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .symbol("MSFT")
                    .name("Microsoft Corporation")
                    .assetType("EQUITY")
                    .units(new BigDecimal("15.0000"))
                    .investedAmount(new BigDecimal("5700.00"))
                    .currentNavOrPrice(new BigDecimal("445.2000"))
                    .status("ACTIVE")
                    .build();
            Investment savedMsft = investmentRepository.save(msft);

            Transaction msftTx = Transaction.builder()
                    .investment(savedMsft)
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .type("BUY")
                    .units(new BigDecimal("15.0000"))
                    .pricePerUnit(new BigDecimal("380.0000"))
                    .totalAmount(new BigDecimal("5700.00"))
                    .transactionDate(LocalDateTime.now().minusMonths(5))
                    .status("COMPLETED")
                    .build();
            transactionRepository.save(msftTx);

            // 3. Vanguard S&P 500 ETF (VOO)
            Investment voo = Investment.builder()
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .symbol("VOO")
                    .name("Vanguard S&P 500 ETF")
                    .assetType("MUTUAL_FUND")
                    .units(new BigDecimal("40.0000"))
                    .investedAmount(new BigDecimal("16400.00"))
                    .currentNavOrPrice(new BigDecimal("512.8000"))
                    .status("ACTIVE")
                    .build();
            Investment savedVoo = investmentRepository.save(voo);

            Transaction vooTx = Transaction.builder()
                    .investment(savedVoo)
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .type("BUY")
                    .units(new BigDecimal("40.0000"))
                    .pricePerUnit(new BigDecimal("410.0000"))
                    .totalAmount(new BigDecimal("16400.00"))
                    .transactionDate(LocalDateTime.now().minusMonths(8))
                    .status("COMPLETED")
                    .build();
            transactionRepository.save(vooTx);

            // 4. NVIDIA Corporation
            Investment nvda = Investment.builder()
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .symbol("NVDA")
                    .name("NVIDIA Corporation")
                    .assetType("EQUITY")
                    .units(new BigDecimal("30.0000"))
                    .investedAmount(new BigDecimal("2850.00"))
                    .currentNavOrPrice(new BigDecimal("132.5000"))
                    .status("ACTIVE")
                    .build();
            Investment savedNvda = investmentRepository.save(nvda);

            Transaction nvdaTx = Transaction.builder()
                    .investment(savedNvda)
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .type("BUY")
                    .units(new BigDecimal("30.0000"))
                    .pricePerUnit(new BigDecimal("95.0000"))
                    .totalAmount(new BigDecimal("2850.00"))
                    .transactionDate(LocalDateTime.now().minusMonths(4))
                    .status("COMPLETED")
                    .build();
            transactionRepository.save(nvdaTx);

            // 5. Seed SIP Schedules
            Sip sipVoo = Sip.builder()
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .symbol("VOO")
                    .name("Vanguard S&P 500 Monthly SIP")
                    .frequency("MONTHLY")
                    .installmentAmount(new BigDecimal("500.00"))
                    .dayOfMonth(1)
                    .nextExecutionDate(LocalDate.now().plusMonths(1).withDayOfMonth(1))
                    .status("ACTIVE")
                    .totalInvested(new BigDecimal("4000.00"))
                    .build();

            Sip sipMsft = Sip.builder()
                    .portfolioId(portfolioId)
                    .userId(userId)
                    .symbol("MSFT")
                    .name("Microsoft Accumulation Plan")
                    .frequency("MONTHLY")
                    .installmentAmount(new BigDecimal("250.00"))
                    .dayOfMonth(15)
                    .nextExecutionDate(LocalDate.now().plusMonths(1).withDayOfMonth(15))
                    .status("ACTIVE")
                    .totalInvested(new BigDecimal("1250.00"))
                    .build();

            sipRepository.saveAll(List.of(sipVoo, sipMsft));

            log.info("Successfully seeded 4 investments, 5 transactions, and 2 SIP schedules.");
        }
    }
}
