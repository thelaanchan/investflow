package com.investflow.portfolio.repository;

import com.investflow.portfolio.model.Portfolio;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, Long> {
    List<Portfolio> findByUserId(Long userId);

    @EntityGraph(attributePaths = {"holdings"})
    Optional<Portfolio> findByIdAndUserId(Long id, Long userId);
}
