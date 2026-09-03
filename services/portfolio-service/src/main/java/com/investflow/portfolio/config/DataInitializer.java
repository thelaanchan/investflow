package com.investflow.portfolio.config;

import com.investflow.portfolio.model.Holding;
import com.investflow.portfolio.model.Portfolio;
import com.investflow.portfolio.repository.HoldingRepository;
import com.investflow.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PortfolioRepository portfolioRepository;
    private final HoldingRepository holdingRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (portfolioRepository.count() == 0) {
            log.info("Seeding demo portfolio and holdings data...");

            Portfolio corePortfolio = Portfolio.builder()
                    .userId(2L) // Alex Mercer (demo user)
                    .name("Core Growth Wealth")
                    .description("High-conviction large cap tech and diversified index ETF portfolio")
                    .type("GROWTH")
                    .build();
            Portfolio savedCore = portfolioRepository.save(corePortfolio);

            Holding aapl = Holding.builder()
                    .portfolio(savedCore)
                    .assetSymbol("AAPL")
                    .assetName("Apple Inc.")
                    .assetType("EQUITY")
                    .quantity(new BigDecimal("25.0000"))
                    .averageBuyPrice(new BigDecimal("175.5000"))
                    .currentPrice(new BigDecimal("228.4000"))
                    .build();

            Holding msft = Holding.builder()
                    .portfolio(savedCore)
                    .assetSymbol("MSFT")
                    .assetName("Microsoft Corporation")
                    .assetType("EQUITY")
                    .quantity(new BigDecimal("15.0000"))
                    .averageBuyPrice(new BigDecimal("380.0000"))
                    .currentPrice(new BigDecimal("445.2000"))
                    .build();

            Holding voo = Holding.builder()
                    .portfolio(savedCore)
                    .assetSymbol("VOO")
                    .assetName("Vanguard S&P 500 ETF")
                    .assetType("MUTUAL_FUND")
                    .quantity(new BigDecimal("40.0000"))
                    .averageBuyPrice(new BigDecimal("410.0000"))
                    .currentPrice(new BigDecimal("512.8000"))
                    .build();

            Holding nvda = Holding.builder()
                    .portfolio(savedCore)
                    .assetSymbol("NVDA")
                    .assetName("NVIDIA Corporation")
                    .assetType("EQUITY")
                    .quantity(new BigDecimal("30.0000"))
                    .averageBuyPrice(new BigDecimal("95.0000"))
                    .currentPrice(new BigDecimal("132.5000"))
                    .build();

            holdingRepository.saveAll(List.of(aapl, msft, voo, nvda));

            Portfolio retirementPortfolio = Portfolio.builder()
                    .userId(2L)
                    .name("Retirement 2050")
                    .description("Balanced retirement fund with index equities and fixed income bonds")
                    .type("BALANCED")
                    .build();
            Portfolio savedRetirement = portfolioRepository.save(retirementPortfolio);

            Holding vti = Holding.builder()
                    .portfolio(savedRetirement)
                    .assetSymbol("VTI")
                    .assetName("Vanguard Total Stock Market ETF")
                    .assetType("MUTUAL_FUND")
                    .quantity(new BigDecimal("100.0000"))
                    .averageBuyPrice(new BigDecimal("210.0000"))
                    .currentPrice(new BigDecimal("265.3000"))
                    .build();

            Holding bnd = Holding.builder()
                    .portfolio(savedRetirement)
                    .assetSymbol("BND")
                    .assetName("Vanguard Total Bond Market ETF")
                    .assetType("BOND")
                    .quantity(new BigDecimal("150.0000"))
                    .averageBuyPrice(new BigDecimal("72.0000"))
                    .currentPrice(new BigDecimal("74.5000"))
                    .build();

            holdingRepository.saveAll(List.of(vti, bnd));

            log.info("Portfolio sample data seeded successfully with 2 portfolios and 6 holdings.");
        }
    }
}
