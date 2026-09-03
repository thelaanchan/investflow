package com.investflow.portfolio.controller;

import com.investflow.portfolio.dto.*;
import com.investflow.portfolio.service.HoldingService;
import com.investflow.portfolio.service.PortfolioService;
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
@RequestMapping("/api/portfolios")
@RequiredArgsConstructor
@Tag(name = "Portfolios", description = "Portfolio and Holdings management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class PortfolioController {

    private final PortfolioService portfolioService;
    private final HoldingService holdingService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        if (userId == null) {
            // Fallback to default demo user ID if attribute not set (e.g. mock/test)
            return 2L;
        }
        return userId;
    }

    @PostMapping
    @Operation(summary = "Create a new portfolio")
    public ResponseEntity<ApiResponse<PortfolioResponse>> createPortfolio(
            HttpServletRequest request,
            @Valid @RequestBody PortfolioRequest body) {
        PortfolioResponse response = portfolioService.createPortfolio(getUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Portfolio created successfully"));
    }

    @GetMapping
    @Operation(summary = "Get all portfolios for current user")
    public ResponseEntity<ApiResponse<List<PortfolioResponse>>> getUserPortfolios(HttpServletRequest request) {
        List<PortfolioResponse> portfolios = portfolioService.getUserPortfolios(getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(portfolios, "Portfolios retrieved successfully"));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get portfolio details by ID")
    public ResponseEntity<ApiResponse<PortfolioResponse>> getPortfolioById(
            HttpServletRequest request,
            @PathVariable Long id) {
        PortfolioResponse response = portfolioService.getPortfolioById(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(response, "Portfolio retrieved successfully"));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update portfolio by ID")
    public ResponseEntity<ApiResponse<PortfolioResponse>> updatePortfolio(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody PortfolioRequest body) {
        PortfolioResponse response = portfolioService.updatePortfolio(id, getUserId(request), body);
        return ResponseEntity.ok(ApiResponse.success(response, "Portfolio updated successfully"));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete portfolio by ID")
    public ResponseEntity<ApiResponse<Void>> deletePortfolio(
            HttpServletRequest request,
            @PathVariable Long id) {
        portfolioService.deletePortfolio(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(null, "Portfolio deleted successfully"));
    }

    @GetMapping("/{id}/holdings")
    @Operation(summary = "List all holdings in a portfolio")
    public ResponseEntity<ApiResponse<List<HoldingResponse>>> getHoldings(
            HttpServletRequest request,
            @PathVariable Long id) {
        List<HoldingResponse> holdings = holdingService.getHoldings(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(holdings, "Holdings retrieved successfully"));
    }

    @PostMapping("/{id}/holdings")
    @Operation(summary = "Add or update holding in a portfolio")
    public ResponseEntity<ApiResponse<HoldingResponse>> addHolding(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody HoldingRequest body) {
        HoldingResponse response = holdingService.addOrUpdateHolding(id, getUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "Holding updated successfully"));
    }

    @DeleteMapping("/{id}/holdings/{holdingId}")
    @Operation(summary = "Remove holding from portfolio")
    public ResponseEntity<ApiResponse<Void>> deleteHolding(
            HttpServletRequest request,
            @PathVariable Long id,
            @PathVariable Long holdingId) {
        holdingService.deleteHolding(id, holdingId, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(null, "Holding deleted successfully"));
    }

    @GetMapping("/{id}/allocation")
    @Operation(summary = "Get asset allocation breakdown of a portfolio")
    public ResponseEntity<ApiResponse<AssetAllocationResponse>> getAllocation(
            HttpServletRequest request,
            @PathVariable Long id) {
        AssetAllocationResponse allocation = portfolioService.getAssetAllocation(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(allocation, "Asset allocation retrieved successfully"));
    }
}
