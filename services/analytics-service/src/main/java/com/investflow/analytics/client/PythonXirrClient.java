package com.investflow.analytics.client;

import com.investflow.analytics.dto.CashFlowDto;
import com.investflow.analytics.dto.XirrRequestDto;
import com.investflow.analytics.dto.XirrResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Component
public class PythonXirrClient {

    private final RestClient restClient;
    private final String xirrServiceUrl;

    public PythonXirrClient(@Value("${services.xirr.url:http://localhost:8005}") String xirrServiceUrl) {
        this.xirrServiceUrl = xirrServiceUrl;
        this.restClient = RestClient.builder().baseUrl(xirrServiceUrl).build();
    }

    public BigDecimal calculateXirr(List<CashFlowDto> cashFlows) {
        if (cashFlows == null || cashFlows.size() < 2) {
            return BigDecimal.ZERO;
        }

        try {
            XirrRequestDto request = XirrRequestDto.builder().cashFlows(cashFlows).build();
            XirrResponseDto response = restClient.post()
                    .uri("/calculate/xirr")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(XirrResponseDto.class);

            if (response != null && response.getAnnualizedPercentage() != null) {
                log.info("XIRR calculated via Python service: {}%", response.getAnnualizedPercentage());
                return response.getAnnualizedPercentage();
            }
        } catch (Exception ex) {
            log.warn("Python XIRR service unavailable at {}. Falling back to internal calculation: {}", xirrServiceUrl, ex.getMessage());
        }

        return fallbackXirr(cashFlows);
    }

    private BigDecimal fallbackXirr(List<CashFlowDto> cashFlows) {
        // Fallback approximation: (Total Value - Total Invested) / Total Invested annualized
        BigDecimal totalOutflows = BigDecimal.ZERO;
        BigDecimal totalInflows = BigDecimal.ZERO;

        for (CashFlowDto cf : cashFlows) {
            if (cf.getAmount().compareTo(BigDecimal.ZERO) < 0) {
                totalOutflows = totalOutflows.add(cf.getAmount().abs());
            } else {
                totalInflows = totalInflows.add(cf.getAmount());
            }
        }

        if (totalOutflows.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        return totalInflows.subtract(totalOutflows)
                .multiply(BigDecimal.valueOf(100))
                .divide(totalOutflows, 2, RoundingMode.HALF_UP);
    }
}
