package com.investflow.investment.controller;

import com.investflow.investment.dto.ApiResponse;
import com.investflow.investment.dto.SipRequest;
import com.investflow.investment.dto.SipResponse;
import com.investflow.investment.service.SipService;
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
@RequestMapping("/api/sips")
@RequiredArgsConstructor
@Tag(name = "Systematic Investment Plans (SIPs)", description = "Automated recurring investment schedules")
@SecurityRequirement(name = "bearerAuth")
public class SipController {

    private final SipService sipService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        return userId != null ? userId : 2L;
    }

    @PostMapping
    @Operation(summary = "Create a recurring SIP schedule")
    public ResponseEntity<ApiResponse<SipResponse>> createSip(
            HttpServletRequest request,
            @Valid @RequestBody SipRequest body) {
        SipResponse response = sipService.createSip(getUserId(request), body);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "SIP schedule created successfully"));
    }

    @GetMapping
    @Operation(summary = "List all SIPs for current user")
    public ResponseEntity<ApiResponse<List<SipResponse>>> getUserSips(
            HttpServletRequest request,
            @RequestParam(required = false) Long portfolioId) {
        List<SipResponse> list = portfolioId != null
                ? sipService.getPortfolioSips(portfolioId, getUserId(request))
                : sipService.getUserSips(getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(list, "SIP schedules retrieved successfully"));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Pause, resume, or cancel a SIP schedule")
    public ResponseEntity<ApiResponse<SipResponse>> updateStatus(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestParam String status) {
        SipResponse response = sipService.updateStatus(id, getUserId(request), status);
        return ResponseEntity.ok(ApiResponse.success(response, "SIP status updated successfully"));
    }

    @PostMapping("/{id}/execute")
    @Operation(summary = "Trigger execution of a SIP installment")
    public ResponseEntity<ApiResponse<SipResponse>> executeSip(
            HttpServletRequest request,
            @PathVariable Long id) {
        SipResponse response = sipService.executeSip(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(response, "SIP installment executed successfully"));
    }
}
