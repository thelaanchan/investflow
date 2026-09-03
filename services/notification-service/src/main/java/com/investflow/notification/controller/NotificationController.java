package com.investflow.notification.controller;

import com.investflow.notification.dto.ApiResponse;
import com.investflow.notification.dto.NotificationRequest;
import com.investflow.notification.dto.NotificationResponse;
import com.investflow.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications & Real-time Alerts", description = "WebSocket push alerts and notification inbox management")
@SecurityRequirement(name = "bearerAuth")
public class NotificationController {

    private final NotificationService notificationService;

    private Long getUserId(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("authenticatedUserId");
        return userId != null ? userId : 2L;
    }

    @GetMapping
    @Operation(summary = "Get notifications for authenticated user")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getNotifications(
            HttpServletRequest request,
            @RequestParam(required = false, defaultValue = "false") boolean unreadOnly) {
        List<NotificationResponse> list = notificationService.getUserNotifications(getUserId(request), unreadOnly);
        return ResponseEntity.ok(ApiResponse.success(list, "Notifications retrieved successfully"));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark specific notification as read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            HttpServletRequest request,
            @PathVariable Long id) {
        NotificationResponse response = notificationService.markAsRead(id, getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(response, "Notification marked as read"));
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(HttpServletRequest request) {
        notificationService.markAllAsRead(getUserId(request));
        return ResponseEntity.ok(ApiResponse.success(null, "All notifications marked as read"));
    }

    @PostMapping("/broadcast")
    @Operation(summary = "Dispatch a new notification and trigger real-time WebSocket push")
    public ResponseEntity<ApiResponse<NotificationResponse>> broadcast(
            @Valid @RequestBody NotificationRequest body) {
        NotificationResponse response = notificationService.sendNotification(body);
        return ResponseEntity.ok(ApiResponse.success(response, "Notification broadcasted successfully"));
    }
}
