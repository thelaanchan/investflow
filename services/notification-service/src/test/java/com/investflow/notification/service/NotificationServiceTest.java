package com.investflow.notification.service;

import com.investflow.notification.dto.NotificationRequest;
import com.investflow.notification.dto.NotificationResponse;
import com.investflow.notification.model.Notification;
import com.investflow.notification.repository.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendNotification_ShouldSaveAndBroadcast() {
        NotificationRequest request = NotificationRequest.builder()
                .userId(2L)
                .title("Trade Executed")
                .message("Bought 10 AAPL")
                .type("TRANSACTION")
                .build();

        Notification saved = Notification.builder()
                .id(1L)
                .userId(2L)
                .title("Trade Executed")
                .message("Bought 10 AAPL")
                .type("TRANSACTION")
                .readStatus(false)
                .createdAt(LocalDateTime.now())
                .build();

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        NotificationResponse response = notificationService.sendNotification(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Trade Executed", response.getTitle());
        verify(notificationRepository).save(any(Notification.class));
        verify(messagingTemplate).convertAndSend(eq("/topic/notifications/2"), any(NotificationResponse.class));
    }
}
