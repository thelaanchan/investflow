package com.investflow.analytics.repository;

import com.investflow.analytics.model.PerformanceSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PerformanceSnapshotRepository extends JpaRepository<PerformanceSnapshot, Long> {
    List<PerformanceSnapshot> findByPortfolioIdOrderBySnapshotDateAsc(Long portfolioId);
}
