package com.investflow.analytics.service;

import com.investflow.analytics.client.PythonXirrClient;
import com.investflow.analytics.dto.PerformanceMetricsResponse;
import com.investflow.analytics.dto.PortfolioAnalyticsResponse;
import com.investflow.analytics.dto.PortfolioXRayResponse;
import com.investflow.analytics.repository.PerformanceSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private PythonXirrClient pythonXirrClient;

    @Mock
    private PerformanceSnapshotRepository snapshotRepository;

    @InjectMocks
    private AnalyticsService analyticsService;

    @Test
    void getPortfolioAnalytics_ShouldReturnCalculatedMetrics() {
        when(pythonXirrClient.calculateXirr(anyList())).thenReturn(new BigDecimal("21.50"));

        PortfolioAnalyticsResponse response = analyticsService.getPortfolioAnalytics(1L, 2L);

        assertNotNull(response);
        assertEquals(1L, response.getPortfolioId());
        assertEquals(new BigDecimal("21.50"), response.getXirrPercentage());
        assertNotNull(response.getTotalInvested());
        assertNotNull(response.getCurrentValue());
    }

    @Test
    void getPerformanceMetrics_ShouldReturnTimelineAndBenchmark() {
        when(snapshotRepository.findByPortfolioIdOrderBySnapshotDateAsc(1L)).thenReturn(Collections.emptyList());

        PerformanceMetricsResponse response = analyticsService.getPerformanceMetrics(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPortfolioId());
        assertFalse(response.getTimeline().isEmpty());
        assertNotNull(response.getAlpha());
        assertNotNull(response.getSharpeRatio());
    }

    @Test
    void getPortfolioXRay_ShouldReturnDiversifiedBreakdown() {
        PortfolioXRayResponse response = analyticsService.getPortfolioXRay(1L);

        assertNotNull(response);
        assertEquals(1L, response.getPortfolioId());
        assertFalse(response.getSectorExposure().isEmpty());
        assertFalse(response.getMarketCapDistribution().isEmpty());
        assertTrue(response.getPortfolioDiversificationScore().compareTo(BigDecimal.ZERO) > 0);
    }
}
