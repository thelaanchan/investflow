package com.investflow.investment.service;

import com.investflow.investment.dto.InvestmentRequest;
import com.investflow.investment.dto.InvestmentResponse;
import com.investflow.investment.dto.TradeRequest;
import com.investflow.investment.dto.TransactionResponse;
import com.investflow.investment.exception.BadRequestException;
import com.investflow.investment.exception.ForbiddenException;
import com.investflow.investment.exception.ResourceNotFoundException;
import com.investflow.investment.model.Investment;
import com.investflow.investment.model.Transaction;
import com.investflow.investment.repository.InvestmentRepository;
import com.investflow.investment.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InvestmentService {

    private final InvestmentRepository investmentRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public InvestmentResponse createInvestment(Long userId, InvestmentRequest request) {
        String symbol = request.getSymbol().trim().toUpperCase();
        BigDecimal totalInitialAmount = request.getUnits().multiply(request.getPricePerUnit()).setScale(2, RoundingMode.HALF_UP);

        Investment investment = Investment.builder()
                .userId(userId)
                .portfolioId(request.getPortfolioId())
                .symbol(symbol)
                .name(request.getName().trim())
                .assetType(request.getAssetType().toUpperCase())
                .units(request.getUnits())
                .investedAmount(totalInitialAmount)
                .currentNavOrPrice(request.getPricePerUnit())
                .status("ACTIVE")
                .build();

        Investment saved = investmentRepository.save(investment);

        Transaction initialTx = Transaction.builder()
                .investment(saved)
                .portfolioId(saved.getPortfolioId())
                .userId(userId)
                .type("BUY")
                .units(request.getUnits())
                .pricePerUnit(request.getPricePerUnit())
                .totalAmount(totalInitialAmount)
                .transactionDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();

        transactionRepository.save(initialTx);

        log.info("Created investment: {} (id: {}) for user: {}", symbol, saved.getId(), userId);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponse> getUserInvestments(Long userId) {
        return investmentRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InvestmentResponse> getPortfolioInvestments(Long portfolioId, Long userId) {
        return investmentRepository.findByPortfolioId(portfolioId).stream()
                .filter(inv -> inv.getUserId().equals(userId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvestmentResponse getInvestmentById(Long id, Long userId) {
        Investment investment = findAndValidate(id, userId);
        return mapToResponse(investment);
    }

    @Transactional
    public InvestmentResponse buy(Long id, Long userId, TradeRequest request) {
        Investment investment = findAndValidate(id, userId);

        BigDecimal tradeTotal = request.getUnits().multiply(request.getPrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal newUnits = investment.getUnits().add(request.getUnits());
        BigDecimal newInvestedAmount = investment.getInvestedAmount().add(tradeTotal);

        investment.setUnits(newUnits);
        investment.setInvestedAmount(newInvestedAmount);
        investment.setCurrentNavOrPrice(request.getPrice());
        investment.setStatus("ACTIVE");

        Investment updated = investmentRepository.save(investment);

        Transaction tx = Transaction.builder()
                .investment(updated)
                .portfolioId(updated.getPortfolioId())
                .userId(userId)
                .type("BUY")
                .units(request.getUnits())
                .pricePerUnit(request.getPrice())
                .totalAmount(tradeTotal)
                .transactionDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();

        transactionRepository.save(tx);
        log.info("Executed BUY of {} units of {} for user {}", request.getUnits(), updated.getSymbol(), userId);

        return mapToResponse(updated);
    }

    @Transactional
    public InvestmentResponse sell(Long id, Long userId, TradeRequest request) {
        Investment investment = findAndValidate(id, userId);

        if (request.getUnits().compareTo(investment.getUnits()) > 0) {
            throw new BadRequestException("Insufficient units to sell. Available: " + investment.getUnits() + ", Requested: " + request.getUnits());
        }

        BigDecimal tradeTotal = request.getUnits().multiply(request.getPrice()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal fraction = request.getUnits().divide(investment.getUnits(), 6, RoundingMode.HALF_UP);
        BigDecimal costBasisRemoved = investment.getInvestedAmount().multiply(fraction).setScale(2, RoundingMode.HALF_UP);

        BigDecimal newUnits = investment.getUnits().subtract(request.getUnits());
        BigDecimal newInvestedAmount = investment.getInvestedAmount().subtract(costBasisRemoved);
        if (newUnits.compareTo(BigDecimal.ZERO) == 0) {
            newInvestedAmount = BigDecimal.ZERO;
            investment.setStatus("EXITED");
        }

        investment.setUnits(newUnits);
        investment.setInvestedAmount(newInvestedAmount);
        investment.setCurrentNavOrPrice(request.getPrice());

        Investment updated = investmentRepository.save(investment);

        Transaction tx = Transaction.builder()
                .investment(updated)
                .portfolioId(updated.getPortfolioId())
                .userId(userId)
                .type("SELL")
                .units(request.getUnits())
                .pricePerUnit(request.getPrice())
                .totalAmount(tradeTotal)
                .transactionDate(LocalDateTime.now())
                .status("COMPLETED")
                .build();

        transactionRepository.save(tx);
        log.info("Executed SELL of {} units of {} for user {}", request.getUnits(), updated.getSymbol(), userId);

        return mapToResponse(updated);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getTransactions(Long investmentId, Long userId) {
        findAndValidate(investmentId, userId);
        return transactionRepository.findByInvestmentIdOrderByTransactionDateDesc(investmentId).stream()
                .map(this::mapTransactionToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> getPortfolioTransactions(Long portfolioId, Long userId) {
        return transactionRepository.findByPortfolioIdOrderByTransactionDateDesc(portfolioId).stream()
                .filter(tx -> tx.getUserId().equals(userId))
                .map(this::mapTransactionToResponse)
                .collect(Collectors.toList());
    }

    private Investment findAndValidate(Long id, Long userId) {
        Investment investment = investmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Investment not found with id: " + id));

        if (!investment.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied: You do not own investment " + id);
        }
        return investment;
    }

    private InvestmentResponse mapToResponse(Investment inv) {
        return InvestmentResponse.builder()
                .id(inv.getId())
                .portfolioId(inv.getPortfolioId())
                .userId(inv.getUserId())
                .symbol(inv.getSymbol())
                .name(inv.getName())
                .assetType(inv.getAssetType())
                .units(inv.getUnits())
                .investedAmount(inv.getInvestedAmount())
                .currentNavOrPrice(inv.getCurrentNavOrPrice())
                .currentValue(inv.getCurrentValue())
                .profitOrLoss(inv.getProfitOrLoss())
                .returnsPercentage(inv.getReturnsPercentage())
                .status(inv.getStatus())
                .createdAt(inv.getCreatedAt())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }

    private TransactionResponse mapTransactionToResponse(Transaction tx) {
        return TransactionResponse.builder()
                .id(tx.getId())
                .investmentId(tx.getInvestment().getId())
                .portfolioId(tx.getPortfolioId())
                .userId(tx.getUserId())
                .type(tx.getType())
                .units(tx.getUnits())
                .pricePerUnit(tx.getPricePerUnit())
                .totalAmount(tx.getTotalAmount())
                .transactionDate(tx.getTransactionDate())
                .status(tx.getStatus())
                .build();
    }
}
