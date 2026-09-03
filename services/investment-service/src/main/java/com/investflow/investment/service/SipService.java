package com.investflow.investment.service;

import com.investflow.investment.dto.SipRequest;
import com.investflow.investment.dto.SipResponse;
import com.investflow.investment.dto.TradeRequest;
import com.investflow.investment.exception.ForbiddenException;
import com.investflow.investment.exception.ResourceNotFoundException;
import com.investflow.investment.model.Investment;
import com.investflow.investment.model.Sip;
import com.investflow.investment.repository.InvestmentRepository;
import com.investflow.investment.repository.SipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SipService {

    private final SipRepository sipRepository;
    private final InvestmentRepository investmentRepository;
    private final InvestmentService investmentService;

    @Transactional
    public SipResponse createSip(Long userId, SipRequest request) {
        String symbol = request.getSymbol().trim().toUpperCase();
        LocalDate nextDate = calculateInitialNextExecution(request.getDayOfMonth());

        Sip sip = Sip.builder()
                .userId(userId)
                .portfolioId(request.getPortfolioId())
                .symbol(symbol)
                .name(request.getName().trim())
                .frequency(request.getFrequency().toUpperCase())
                .installmentAmount(request.getInstallmentAmount())
                .dayOfMonth(request.getDayOfMonth())
                .nextExecutionDate(nextDate)
                .status("ACTIVE")
                .totalInvested(BigDecimal.ZERO)
                .build();

        Sip saved = sipRepository.save(sip);
        log.info("Created SIP schedule for {} in portfolio {} by user {}", symbol, request.getPortfolioId(), userId);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<SipResponse> getUserSips(Long userId) {
        return sipRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SipResponse> getPortfolioSips(Long portfolioId, Long userId) {
        return sipRepository.findByPortfolioId(portfolioId).stream()
                .filter(s -> s.getUserId().equals(userId))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public SipResponse updateStatus(Long id, Long userId, String status) {
        Sip sip = findAndValidate(id, userId);
        sip.setStatus(status.toUpperCase());
        Sip updated = sipRepository.save(sip);
        log.info("Updated SIP {} status to {}", id, status);
        return mapToResponse(updated);
    }

    @Transactional
    public SipResponse executeSip(Long id, Long userId) {
        Sip sip = findAndValidate(id, userId);

        // Find or create underlying investment
        Optional<Investment> existingInv = investmentRepository.findByPortfolioIdAndSymbol(sip.getPortfolioId(), sip.getSymbol());

        BigDecimal marketPrice = existingInv.map(Investment::getCurrentNavOrPrice)
                .filter(p -> p.compareTo(BigDecimal.ZERO) > 0)
                .orElse(new BigDecimal("100.00")); // default market NAV if new

        BigDecimal unitsToBuy = sip.getInstallmentAmount().divide(marketPrice, 4, RoundingMode.HALF_UP);

        Investment targetInvestment;
        if (existingInv.isPresent()) {
            targetInvestment = existingInv.get();
            investmentService.buy(targetInvestment.getId(), userId, TradeRequest.builder()
                    .units(unitsToBuy)
                    .price(marketPrice)
                    .build());
        } else {
            targetInvestment = investmentRepository.save(Investment.builder()
                    .portfolioId(sip.getPortfolioId())
                    .userId(userId)
                    .symbol(sip.getSymbol())
                    .name(sip.getName())
                    .assetType("MUTUAL_FUND")
                    .units(unitsToBuy)
                    .investedAmount(sip.getInstallmentAmount())
                    .currentNavOrPrice(marketPrice)
                    .status("ACTIVE")
                    .build());
        }

        sip.setTotalInvested(sip.getTotalInvested().add(sip.getInstallmentAmount()));
        sip.setNextExecutionDate(calculateNextExecution(sip.getNextExecutionDate(), sip.getFrequency()));
        Sip updated = sipRepository.save(sip);

        log.info("Executed SIP installment for {} (${}) - next date: {}", sip.getSymbol(), sip.getInstallmentAmount(), sip.getNextExecutionDate());
        return mapToResponse(updated);
    }

    private LocalDate calculateInitialNextExecution(int dayOfMonth) {
        LocalDate now = LocalDate.now();
        LocalDate target = now.withDayOfMonth(Math.min(dayOfMonth, now.lengthOfMonth()));
        if (!target.isAfter(now)) {
            target = target.plusMonths(1).withDayOfMonth(Math.min(dayOfMonth, target.plusMonths(1).lengthOfMonth()));
        }
        return target;
    }

    private LocalDate calculateNextExecution(LocalDate current, String frequency) {
        if ("WEEKLY".equalsIgnoreCase(frequency)) {
            return current.plusWeeks(1);
        }
        return current.plusMonths(1);
    }

    private Sip findAndValidate(Long id, Long userId) {
        Sip sip = sipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SIP schedule not found with id: " + id));
        if (!sip.getUserId().equals(userId)) {
            throw new ForbiddenException("Access denied: You do not own SIP " + id);
        }
        return sip;
    }

    private SipResponse mapToResponse(Sip s) {
        return SipResponse.builder()
                .id(s.getId())
                .portfolioId(s.getPortfolioId())
                .userId(s.getUserId())
                .symbol(s.getSymbol())
                .name(s.getName())
                .frequency(s.getFrequency())
                .installmentAmount(s.getInstallmentAmount())
                .dayOfMonth(s.getDayOfMonth())
                .nextExecutionDate(s.getNextExecutionDate())
                .status(s.getStatus())
                .totalInvested(s.getTotalInvested())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
