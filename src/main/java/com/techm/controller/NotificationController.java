package com.techm.controller;

import com.techm.service.NotificationService;
import com.techm.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NotificationController {

    @Autowired
    private ReminderService reminderService;

    @Autowired
    private NotificationService notificationService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping("/test")
    public ResponseEntity<?> testNotification(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            // Create test notification
            Map<String, Object> notification = new HashMap<>();
            notification.put("title", "Test Notification");
            notification.put("message", "This is a test notification to verify the system is working!");
            notification.put("type", "test");
            notification.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.ok(notification);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/trigger-pending")
    public ResponseEntity<?> triggerPendingNotifications(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);

            // Manually trigger pending reminder processing
            reminderService.processPendingReminders();

            Map<String, String> response = new HashMap<>();
            response.put("message", "Pending reminders processed successfully");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to process pending reminders");
            return ResponseEntity.badRequest().body(error);
        }
    }
}