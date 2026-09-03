package com.investflow.portfolio.service;

import com.investflow.portfolio.dto.HoldingRequest;
import com.investflow.portfolio.dto.HoldingResponse;
import com.investflow.portfolio.exception.ResourceNotFoundException;
import com.investflow.portfolio.model.Holding;
import com.investflow.portfolio.model.Portfolio;
import com.investflow.portfolio.repository.HoldingRepository;
import com.investflow.portfolio.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class HoldingService {

    private final HoldingRepository holdingRepository;
    private final PortfolioRepository portfolioRepository;
    private final PortfolioService portfolioService;

    @Transactional(readOnly = true)
    public List<HoldingResponse> getHoldings(Long portfolioId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        portfolioService.validateOwnership(portfolio, userId);

        return holdingRepository.findByPortfolioId(portfolioId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HoldingResponse addOrUpdateHolding(Long portfolioId, Long userId, HoldingRequest request) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        portfolioService.validateOwnership(portfolio, userId);

        String symbol = request.getAssetSymbol().trim().toUpperCase();
        Optional<Holding> existingOpt = holdingRepository.findByPortfolioIdAndAssetSymbol(portfolioId, symbol);

        Holding holding;
        if (existingOpt.isPresent()) {
            holding = existingOpt.get();
            holding.setQuantity(request.getQuantity());
            holding.setAverageBuyPrice(request.getAverageBuyPrice());
            holding.setCurrentPrice(request.getCurrentPrice());
            holding.setAssetName(request.getAssetName().trim());
            holding.setAssetType(request.getAssetType().toUpperCase());
        } else {
            holding = Holding.builder()
                    .portfolio(portfolio)
                    .assetSymbol(symbol)
                    .assetName(request.getAssetName().trim())
                    .assetType(request.getAssetType().toUpperCase())
                    .quantity(request.getQuantity())
                    .averageBuyPrice(request.getAverageBuyPrice())
                    .currentPrice(request.getCurrentPrice())
                    .build();
        }

        Holding saved = holdingRepository.save(holding);
        log.info("Saved holding {} for portfolio {}", symbol, portfolioId);
        return mapToResponse(saved);
    }

    @Transactional
    public void deleteHolding(Long portfolioId, Long holdingId, Long userId) {
        Portfolio portfolio = portfolioRepository.findById(portfolioId)
                .orElseThrow(() -> new ResourceNotFoundException("Portfolio not found: " + portfolioId));
        portfolioService.validateOwnership(portfolio, userId);

        Holding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found: " + holdingId));

        if (!holding.getPortfolio().getId().equals(portfolioId)) {
            throw new ResourceNotFoundException("Holding " + holdingId + " does not belong to portfolio " + portfolioId);
        }

        holdingRepository.delete(holding);
        log.info("Deleted holding {} from portfolio {}", holdingId, portfolioId);
    }

    private HoldingResponse mapToResponse(Holding h) {
        return HoldingResponse.builder()
                .id(h.getId())
                .portfolioId(h.getPortfolio().getId())
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
                .build();
    }
}
