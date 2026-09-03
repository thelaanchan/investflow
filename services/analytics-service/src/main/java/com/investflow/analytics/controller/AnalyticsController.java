package com.investflow.analytics.controller;

import com.investflow.analytics.dto.*;
import com.investflow.analytics.service.AnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
@Tag(name = "Analytics & Returns", description = "Performance metrics, Portfolio X-Ray, XIRR calculations, and live SSE event stream")
@SecurityRequirement(name = "bearerAuth")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        return userId != null ? userId : 2L;
    }

    @GetMapping("/portfolio/{portfolioId}")
    @Operation(summary = "Get high-level portfolio analytics (Value, P&L, Returns, XIRR, Asset Split)")
    public ResponseEntity<ApiResponse<PortfolioAnalyticsResponse>> getPortfolioAnalytics(
            HttpServletRequest request,
            @PathVariable Long portfolioId) {
        PortfolioAnalyticsResponse response = analyticsService.getPortfolioAnalytics(portfolioId, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(response, "Analytics computed successfully"));
    }

    @GetMapping("/portfolio/{portfolioId}/performance")
    @Operation(summary = "Get detailed performance metrics, benchmark comparisons, and historical timeline")
    public ResponseEntity<ApiResponse<PerformanceMetricsResponse>> getPerformanceMetrics(
            @PathVariable Long portfolioId) {
        PerformanceMetricsResponse response = analyticsService.getPerformanceMetrics(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(response, "Performance metrics retrieved"));
    }

    @GetMapping("/portfolio/{portfolioId}/allocation")
    @Operation(summary = "Get asset allocation summary")
    public ResponseEntity<ApiResponse<Map<String, BigDecimal>>> getAllocation(
            HttpServletRequest request,
            @PathVariable Long portfolioId) {
        PortfolioAnalyticsResponse analytics = analyticsService.getPortfolioAnalytics(portfolioId, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(analytics.getAssetAllocation(), "Allocation retrieved"));
    }

    @GetMapping("/portfolio/{portfolioId}/xray")
    @Operation(summary = "Get Portfolio X-Ray (Sectors, Market Caps, Geography, Diversification score)")
    public ResponseEntity<ApiResponse<PortfolioXRayResponse>> getPortfolioXRay(
            @PathVariable Long portfolioId) {
        PortfolioXRayResponse response = analyticsService.getPortfolioXRay(portfolioId);
        return ResponseEntity.ok(ApiResponse.success(response, "Portfolio X-Ray generated"));
    }

    @PostMapping("/portfolio/{portfolioId}/calculate-xirr")
    @Operation(summary = "Directly compute XIRR for a list of cash flows using the Python calculation engine")
    public ResponseEntity<ApiResponse<BigDecimal>> calculateXirr(
            @PathVariable Long portfolioId,
            @RequestBody XirrRequestDto request) {
        BigDecimal xirr = analyticsService.calculateDirectXirr(request.getCashFlows());
        return ResponseEntity.ok(ApiResponse.success(xirr, "XIRR calculated successfully"));
    }

    @GetMapping(value = "/portfolio/{portfolioId}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Operation(summary = "Subscribe to live Server-Sent Events (SSE) valuation ticks and market updates")
    public SseEmitter subscribeToEvents(@PathVariable Long portfolioId) {
        return analyticsService.subscribeToPortfolioEvents(portfolioId);
    }
}
