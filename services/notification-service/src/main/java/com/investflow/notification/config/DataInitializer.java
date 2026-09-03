package com.investflow.notification.config;

import com.investflow.notification.model.Notification;
import com.investflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final NotificationRepository notificationRepository;

    @Override
    @Transactional
    public void run(String... args) {
        if (notificationRepository.count() == 0) {
            log.info("Seeding initial notifications for demo user...");

            Long userId = 2L; // Alex Mercer

            Notification n1 = Notification.builder()
                    .userId(userId)
                    .title("Welcome to InvestFlow")
                    .message("Your wealth portfolio has been successfully initialized. Start exploring real-time analytics and automated SIPs.")
                    .type("PORTFOLIO_ALERT")
                    .readStatus(true)
                    .build();

            Notification n2 = Notification.builder()
                    .userId(userId)
                    .title("SIP Execution Confirmed")
                    .message("Your recurring installment of $500.00 for Vanguard S&P 500 ETF (VOO) was successfully executed.")
                    .type("SIP_REMINDER")
                    .readStatus(false)
                    .build();

            Notification n3 = Notification.builder()
                    .userId(userId)
                    .title("Portfolio Milestone Reached")
                    .message("Your portfolio returns exceeded 22.0% annualized. Core Growth Wealth continues to outpace benchmark.")
                    .type("MARKET_UPDATE")
                    .readStatus(false)
                    .build();

            notificationRepository.saveAll(List.of(n1, n2, n3));
            log.info("Seeded 3 initial notifications.");
        }
    }
}
