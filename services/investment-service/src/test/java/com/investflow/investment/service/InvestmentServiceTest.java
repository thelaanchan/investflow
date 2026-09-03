package com.investflow.investment.service;

import com.investflow.investment.dto.InvestmentRequest;
import com.investflow.investment.dto.InvestmentResponse;
import com.investflow.investment.dto.TradeRequest;
import com.investflow.investment.exception.BadRequestException;
import com.investflow.investment.model.Investment;
import com.investflow.investment.model.Transaction;
import com.investflow.investment.repository.InvestmentRepository;
import com.investflow.investment.repository.TransactionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {

    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private InvestmentService investmentService;

    @Test
    void createInvestment_ShouldSucceed() {
        InvestmentRequest request = InvestmentRequest.builder()
                .portfolioId(1L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .assetType("EQUITY")
                .units(new BigDecimal("10.0000"))
                .pricePerUnit(new BigDecimal("150.0000"))
                .build();

        Investment saved = Investment.builder()
                .id(1L)
                .portfolioId(1L)
                .userId(2L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .assetType("EQUITY")
                .units(new BigDecimal("10.0000"))
                .investedAmount(new BigDecimal("1500.00"))
                .currentNavOrPrice(new BigDecimal("150.0000"))
                .status("ACTIVE")
                .build();

        when(investmentRepository.save(any(Investment.class))).thenReturn(saved);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(new Transaction());

        InvestmentResponse response = investmentService.createInvestment(2L, request);

        assertNotNull(response);
        assertEquals("AAPL", response.getSymbol());
        assertEquals(new BigDecimal("1500.00"), response.getInvestedAmount());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void buy_ShouldIncreaseUnitsAndInvestedAmount() {
        Investment existing = Investment.builder()
                .id(1L)
                .portfolioId(1L)
                .userId(2L)
                .symbol("AAPL")
                .name("Apple Inc.")
                .units(new BigDecimal("10.0000"))
                .investedAmount(new BigDecimal("1500.00"))
                .currentNavOrPrice(new BigDecimal("150.0000"))
                .status("ACTIVE")
                .build();

        TradeRequest trade = TradeRequest.builder()
                .units(new BigDecimal("5.0000"))
                .price(new BigDecimal("200.0000"))
                .build();

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(investmentRepository.save(any(Investment.class))).thenAnswer(inv -> inv.getArgument(0));

        InvestmentResponse response = investmentService.buy(1L, 2L, trade);

        assertNotNull(response);
        assertEquals(new BigDecimal("15.0000"), response.getUnits());
        assertEquals(new BigDecimal("2500.00"), response.getInvestedAmount());
    }

    @Test
    void sell_ShouldThrowBadRequest_WhenSellingMoreThanAvailable() {
        Investment existing = Investment.builder()
                .id(1L)
                .portfolioId(1L)
                .userId(2L)
                .units(new BigDecimal("10.0000"))
                .investedAmount(new BigDecimal("1500.00"))
                .build();

        TradeRequest trade = TradeRequest.builder()
                .units(new BigDecimal("15.0000"))
                .price(new BigDecimal("200.0000"))
                .build();

        when(investmentRepository.findById(1L)).thenReturn(Optional.of(existing));

        assertThrows(BadRequestException.class, () -> investmentService.sell(1L, 2L, trade));
        verify(investmentRepository, never()).save(any(Investment.class));
    }
}
