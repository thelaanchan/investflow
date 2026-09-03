package com.investflow.investment.controller;

import com.investflow.investment.dto.*;
import com.investflow.investment.service.InvestmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/investments")
@RequiredArgsConstructor
@Tag(name = "Investments", description = "Stock and Mutual Fund trade and position management")
@SecurityRequirement(name = "bearerAuth")
public class InvestmentController {

    private final InvestmentService investmentService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        return userId != null ? userId : 2L;
    }

    @PostMapping
    @Operation(summary = "Create a new investment entry")
    public ResponseEntity<ApiResponse<InvestmentResponse>> createInvestment(
            HttpServletRequest request,
            @Valid @RequestBody InvestmentRequest body) {
        InvestmentResponse response = investmentService.createInvestment(getUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Investment created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all investments for the current user")
    public ResponseEntity<ApiResponse<List<InvestmentResponse>>> getUserInvestments(
            HttpServletRequest request,
            @RequestParam(required = false) Long portfolioId) {
        List<InvestmentResponse> list = portfolioId != null
                ? investmentService.getPortfolioInvestments(portfolioId, getUserId(request))
                : investmentService.getUserInvestments(getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(list, "Investments retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get investment details by ID")
    public ResponseEntity<ApiResponse<InvestmentResponse>> getInvestmentById(
            HttpServletRequest request,
            @PathVariable Long id) {
        InvestmentResponse response = investmentService.getInvestmentById(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(response, "Investment retrieved successfully"));
    }

    @PostMapping("/{id}/buy")
    @Operation(summary = "Execute a BUY order on an existing investment")
    public ResponseEntity<ApiResponse<InvestmentResponse>> buy(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody TradeRequest body) {
        InvestmentResponse response = investmentService.buy(id, getUserId(request), body);
        return ResponseEntity.ok(ApiResponse.success(response, "BUY order executed successfully"));
    }

    @PostMapping("/{id}/sell")
    @Operation(summary = "Execute a SELL order on an existing investment")
    public ResponseEntity<ApiResponse<InvestmentResponse>> sell(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody TradeRequest body) {
        InvestmentResponse response = investmentService.sell(id, getUserId(request), body);
        return ResponseEntity.ok(ApiResponse.success(response, "SELL order executed successfully"));
    }

    @GetMapping("/{id}/transactions")
    @Operation(summary = "Get audit trail of transactions for an investment")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactions(
            HttpServletRequest request,
            @PathVariable Long id) {
        List<TransactionResponse> txs = investmentService.getTransactions(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(txs, "Transactions retrieved successfully"));
    }
}
