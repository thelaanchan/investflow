package com.investflow.notification.service;

import com.investflow.notification.dto.NotificationRequest;
import com.investflow.notification.dto.NotificationResponse;
import com.investflow.notification.exception.ResourceNotFoundException;
import com.investflow.notification.model.Notification;
import com.investflow.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public NotificationResponse sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .userId(request.getUserId())
                .title(request.getTitle().trim())
                .message(request.getMessage().trim())
                .type(request.getType().toUpperCase())
                .readStatus(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = mapToResponse(saved);

        // Real-time WebSocket push via STOMP broker
        try {
            String destination = "/topic/notifications/" + request.getUserId();
            messagingTemplate.convertAndSend(destination, response);
            log.info("Broadcasted notification via WebSocket to destination: {}", destination);
        } catch (Exception ex) {
            log.warn("Failed to push WebSocket notification: {}", ex.getMessage());
        }

        return response;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> getUserNotifications(Long userId, boolean unreadOnly) {
        List<Notification> list = unreadOnly
                ? notificationRepository.findByUserIdAndReadStatusFalseOrderByCreatedAtDesc(userId)
                : notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return list.stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public NotificationResponse markAsRead(Long id, Long userId) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found with id: " + id));

        if (!notification.getUserId().equals(userId)) {
            throw new ResourceNotFoundException("Notification does not belong to user: " + userId);
        }

        notification.setReadStatus(true);
        Notification updated = notificationRepository.save(notification);
        return mapToResponse(updated);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndReadStatusFalseOrderByCreatedAtDesc(userId);
        for (Notification n : unread) {
            n.setReadStatus(true);
        }
        notificationRepository.saveAll(unread);
        log.info("Marked {} notifications as read for user: {}", unread.size(), userId);
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .userId(n.getUserId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType())
                .readStatus(n.isReadStatus())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
