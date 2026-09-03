package com.investflow.portfolio.service;

import com.investflow.portfolio.dto.AssetAllocationResponse;
import com.investflow.portfolio.dto.HoldingResponse;
import com.investflow.portfolio.dto.PortfolioRequest;
import com.investflow.portfolio.dto.PortfolioResponse;
import com.investflow.portfolio.exception.ForbiddenException;
import com.investflow.portfolio.exception.ResourceNotFoundException;
import com.investflow.portfolio.model.Holding;
import com.investflow.portfolio.model.Portfolio;
import com.investflow.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PortfolioService {

    private final PortfolioRepository portfolioRepository;

    @Transactional
    public PortfolioResponse createPortfolio(Long userId, PortfolioRequest request) {
        Portfolio portfolio = Portfolio.builder()
                .userId(userId)
                .name(request.getName().trim())
                .description(request.getDescription() != null ? request.getDescription().trim() : null)
                .type(request.getType() != null ? request.getType().toUpperCase() : "BALANCED")
                .build();

        Portfolio saved = portfolioRepository.save(portfolio);
        log.info("Created portfolio with id: {} for user: {}", saved.getId(), userId);
        return mapToResponse(saved, true);
    }

    @Transactional(readOnly = true)
    public List<PortfolioResponse> getUserPortfolios(Long userId) {
        List<Portfolio> portfolios = portfolioRepository.findByUserId(userId);
        return portfolios.stream()
                .map(p -> mapToResponse(p, false))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolioById(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));

        validateOwnership(portfolio, userId);
        return mapToResponse(portfolio, true);
    }

    @Transactional
    public PortfolioResponse updatePortfolio(Long portfolioId, Long userId, PortfolioRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));

        validateOwnership(portfolio, userId);

        portfolio.setName(request.getName().trim());
        if (request.getDescription() != null) {
            portfolio.setDescription(request.getDescription().trim());
        }
        if (request.getType() != null) {
            portfolio.setType(request.getType().toUpperCase());
        }

        Portfolio updated = portfolioRepository.save(portfolio);
        log.info("Updated portfolio: {} by user: {}", portfolioId, userId);
        return mapToResponse(updated, true);
    }

    @Transactional
    public void deletePortfolio(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));

        validateOwnership(portfolio, userId);
        portfolioRepository.delete(portfolio);
        log.info("Deleted portfolio: {} by user: {}", portfolioId, userId);
    }

    @Transactional(readOnly = true)
    public AssetAllocationResponse getAssetAllocation(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found with id: " + portfolioId));

        validateOwnership(portfolio, userId);

        BigDecimal totalValue = BigDecimal.ZERO;
        Map<String, BigDecimal> valueByType = new HashMap<>();

        for (Holding h : portfolio.getHoldings()) {
            BigDecimal holdingVal = h.getCurrentValue();
            totalValue = totalValue.add(holdingVal);
            valueByType.put(h.getAssetType(),
                    valueByType.getOrDefault(h.getAssetType(), BigDecimal.ZERO).add(holdingVal));
        }

        Map<String, BigDecimal> percentageByType = new HashMap<>();
        if (totalValue.compareTo(BigDecimal.ZERO) > 0) {
            for (Map.Entry<String, BigDecimal> entry : valueByType.entrySet()) {
                BigDecimal percentage = entry.getValue()
                        .multiply(BigDecimal.valueOf(100))
                        .divide(totalValue, 2, RoundingMode.HALF_UP);
                percentageByType.put(entry.getKey(), percentage);
            }
        }

        return AssetAllocationResponse.builder()
                .portfolioId(portfolioId)
                .totalPortfolioValue(totalValue.setScale(2, RoundingMode.HALF_UP))
                .allocationByAssetType(percentageByType)
                .valueByAssetType(valueByType)
                .build();
    }

    public void validateOwnership(Portfolio portfolio, Long userId) {
        if (!portfolio.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied: You do not own portfolio " + portfolio.getId());
        }
    }

    public PortfolioResponse mapToResponse(Portfolio p, boolean includeHoldings) {
        BigDecimal totalInvested = BigDecimal.ZERO;
        BigDecimal currentValue = BigDecimal.ZERO;

        List<HoldingResponse> holdingResponses = new ArrayList<>();
        if (p.getHoldings() != null) {
            for (Holding h : p.getHoldings()) {
                totalInvested = totalInvested.add(h.getTotalInvested());
                currentValue = currentValue.add(h.getCurrentValue());

                if (includeHoldings) {
                    holdingResponses.add(HoldingResponse.builder()
                            .id(h.getId())
                            .portfolioId(p.getId())
                            .assetSymbol(h.getAssetSymbol())
                            .assetName(h.getAssetName())
                            .assetType(h.getAssetType())
                            .quantity(h.getQuantity())
                            .averageBuyPrice(h.getAverageBuyPrice())
                            .currentPrice(h.getCurrentPrice())
                            .totalInvested(h.getTotalInvested())
                            .currentValue(h.getCurrentValue())
                            .profitOrLoss(h.getProfitOrLoss())
                            .returnsPercentage(h.getReturnsPercentage())
                            .updatedAt(h.getUpdatedAt())
                            .build());
                }
            }
        }

        BigDecimal profitOrLoss = currentValue.subtract(totalInvested).setScale(2, RoundingMode.HALF_UP);
        BigDecimal returnsPercentage = BigDecimal.ZERO;
        if (totalInvested.compareTo(BigDecimal.ZERO) > 0) {
            returnsPercentage = profitOrLoss.multiply(BigDecimal.valueOf(100))
                    .divide(totalInvested, 2, RoundingMode.HALF_UP);
        }

        return PortfolioResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .name(p.getName())
                .description(p.getDescription())
                .type(p.getType())
                .totalInvested(totalInvested.setScale(2, RoundingMode.HALF_UP))
                .currentValue(currentValue.setScale(2, RoundingMode.HALF_UP))
                .totalProfitLoss(profitOrLoss)
                .returnsPercentage(returnsPercentage)
                .holdingsCount(p.getHoldings() != null ? p.getHoldings().size() : 0)
                .holdings(includeHoldings ? holdingResponses : null)
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
