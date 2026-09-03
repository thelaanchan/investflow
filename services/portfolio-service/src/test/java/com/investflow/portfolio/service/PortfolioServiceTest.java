package com.investflow.portfolio.service;

import com.investflow.portfolio.dto.PortfolioRequest;
import com.investflow.portfolio.dto.PortfolioResponse;
import com.investflow.portfolio.exception.ForbiddenException;
import com.investflow.portfolio.model.Portfolio;
import com.investflow.portfolio.repository.PortfolioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PortfolioServiceTest {

    @Mock
    private PortfolioRepository portfolioRepository;

    @InjectMocks
    private PortfolioService portfolioService;

    @Test
    void createPortfolio_ShouldSucceed() {
        PortfolioRequest request = PortfolioRequest.builder()
                .name("Growth 2026")
                .description("Long term tech equities")
                .type("GROWTH")
                .build();

        Portfolio saved = Portfolio.builder()
                .id(10L)
                .userId(2L)
                .name("Growth 2026")
                .description("Long term tech equities")
                .type("GROWTH")
                .build();

        when(portfolioRepository.save(any(Portfolio.class))).thenReturn(saved);

        PortfolioResponse response = portfolioService.createPortfolio(2L, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Growth 2026", response.getName());
        verify(portfolioRepository).save(any(Portfolio.class));
    }

    @Test
    void validateOwnership_ShouldThrowForbidden_WhenUserMismatch() {
        Portfolio portfolio = Portfolio.builder()
                .id(10L)
                .userId(99L)
                .name("Other User Portfolio")
                .build();

        assertThrows(ForbiddenException.class, () -> portfolioService.validateOwnership(portfolio, 2L));
    }

    @Test
    void getPortfolioById_ShouldSucceed_WhenOwnerMatches() {
        Portfolio portfolio = Portfolio.builder()
                .id(10L)
                .userId(2L)
                .name("My Portfolio")
                .build();

        when(portfolioRepository.findById(10L)).thenReturn(Optional.of(portfolio));

        PortfolioResponse response = portfolioService.getPortfolioById(10L, 2L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
    }
}
