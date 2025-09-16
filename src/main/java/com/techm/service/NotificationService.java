package com.techm.service;

import com.techm.model.Reminder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendReminderNotification(Reminder reminder) {
        try {
            // Create notification payload
            Map<String, Object> notification = new HashMap<>();
            notification.put("id", reminder.getReminderId());
            notification.put("title", "Reminder Alert");
            notification.put("message", reminder.getMessage());
            notification.put("noteTitle", getNoteTitle(reminder));
            notification.put("reminderTime", reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            notification.put("type", "reminder");
            notification.put("timestamp", System.currentTimeMillis());

            // Send to specific user channel
            String userChannel = "/topic/notifications/" + reminder.getNote().getUser().getUserId();
            messagingTemplate.convertAndSend(userChannel, notification);

            // Log the notification
            System.out.println("=== REMINDER NOTIFICATION SENT ===");
            System.out.println("User: " + reminder.getNote().getUser().getUsername());
            System.out.println("Channel: " + userChannel);
            System.out.println("Message: " + reminder.getMessage());
            System.out.println("===================================");

        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
            // Fallback to console notification
            sendConsoleNotification(reminder);
        }
    }

    public void sendEmailNotification(Reminder reminder) {
        // TODO: Implement email sending via SMTP
        System.out.println("📧 EMAIL: " + reminder.getMessage() + " to " + reminder.getNote().getUser().getEmail());
    }

    public void sendSMSNotification(Reminder reminder) {
        // TODO: Implement SMS sending
        System.out.println("📱 SMS: " + reminder.getMessage());
    }

    public void sendPushNotification(Reminder reminder) {
        // TODO: Implement push notification
        System.out.println("🔔 PUSH: " + reminder.getMessage());
    }

    private void sendConsoleNotification(Reminder reminder) {
        System.out.println("\n🔔 ============ REMINDER ALERT ============ 🔔");
        System.out.println("📝 Note: " + getNoteTitle(reminder));
        System.out.println("⏰ Time: " + reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        System.out.println("💬 Message: " + reminder.getMessage());
        System.out.println("👤 User: " + reminder.getNote().getUser().getUsername());
        System.out.println("🔔 ========================================= 🔔\n");
    }

    private String getNoteTitle(Reminder reminder) {
        return reminder.getNote().getTitle() != null && !reminder.getNote().getTitle().trim().isEmpty()
                ? reminder.getNote().getTitle() : "Untitled Note";
    }
}