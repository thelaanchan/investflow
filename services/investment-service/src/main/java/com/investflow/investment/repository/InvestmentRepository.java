package com.investflow.investment.repository;

import com.investflow.investment.model.Investment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvestmentRepository extends JpaRepository<Investment, Long> {
    List<Investment> findByUserId(Long userId);
    List<Investment> findByPortfolioId(Long portfolioId);
    Optional<Investment> findByIdAndUserId(Long id, Long userId);
    Optional<Investment> findByPortfolioIdAndSymbol(Long portfolioId, String symbol);
}
