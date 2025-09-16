package com.techm.service;

import com.techm.model.Note;
import com.techm.model.Reminder;
import com.techm.model.User;
import com.techm.repository.ReminderRepository;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository reminderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    // Original methods for backward compatibility
    public List<Reminder> getAll() {
        return reminderRepository.findAll();
    }

    public Reminder getById(Long id) {
        return reminderRepository.findById(id).orElse(null);
    }

    public Reminder save(Reminder obj) {
        return reminderRepository.save(obj);
    }

    public void delete(Long id) {
        reminderRepository.deleteById(id);
    }

    // Authentication helper
    private User getCurrentUser(String token) {
        if (token != null && token.startsWith("simple_")) {
            try {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    Integer userId = Integer.parseInt(parts[1]);
                    return userRepository.findById(userId).orElse(null);
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    // Controller methods
    public List<Reminder> getAllRemindersByUser(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        return reminderRepository.findByNoteUserUserIdOrderByReminderTimeDesc(currentUser.getUserId());
    }

    public Reminder getReminderById(Integer id, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        return reminderRepository.findByReminderIdAndNoteUserUserId(id, currentUser.getUserId()).orElse(null);
    }

    public Reminder saveReminder(Reminder reminder, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify the reminder belongs to a note owned by the current user
        if (reminder.getNote() != null &&
                !reminder.getNote().getUser().getUserId().equals(currentUser.getUserId())) {
            throw new RuntimeException("Access denied: Note does not belong to current user");
        }

        LocalDateTime now = LocalDateTime.now();

        if (reminder.getReminderId() == null) {
            // New reminder
            reminder.setCreatedAt(now);
        }

        reminder.setUpdatedAt(now);

        if (reminder.getIsSent() == null) {
            reminder.setIsSent(false);
        }

        if (reminder.getIsActive() == null) {
            reminder.setIsActive(true);
        }

        return reminderRepository.save(reminder);
    }

    public void deleteReminder(Integer id, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Reminder reminder = reminderRepository.findByReminderIdAndNoteUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Reminder not found or access denied"));

        reminderRepository.delete(reminder);
    }

    public List<Reminder> getPendingReminders(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        LocalDateTime now = LocalDateTime.now();
        return reminderRepository.findByNoteUserUserIdAndIsSentFalseAndReminderTimeBeforeOrderByReminderTimeAsc(
                currentUser.getUserId(), now);
    }

    public Reminder markAsSent(Integer id, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Reminder reminder = reminderRepository.findByReminderIdAndNoteUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Reminder not found or access denied"));

        reminder.setIsSent(true);
        reminder.setSentAt(LocalDateTime.now());
        reminder.setUpdatedAt(LocalDateTime.now());

        return reminderRepository.save(reminder);
    }

    // Auto reminder creation for new notes
    public void createAutoReminders(Note note) {
        try {
            LocalDateTime now = LocalDateTime.now();

            // Check if note content contains time-related keywords and create appropriate reminders
            String content = (note.getContent() != null ? note.getContent() : "") + " " +
                    (note.getTitle() != null ? note.getTitle() : "");

            createSmartReminders(note, content, now);

        } catch (Exception e) {
            System.err.println("Error creating auto reminders: " + e.getMessage());
        }
    }

    private void createSmartReminders(Note note, String content, LocalDateTime baseTime) {
        // Create default reminder (1 day later)
        createReminder(note, baseTime.plusDays(1),
                "Review your note: " + getTitleOrDefault(note), "AUTO");

        // Smart reminders based on content analysis
        String lowerContent = content.toLowerCase();

        // Meeting reminders
        if (containsKeywords(lowerContent, "meeting", "call", "conference", "appointment")) {
            createReminder(note, baseTime.plusHours(1),
                    "Upcoming meeting reminder: " + getTitleOrDefault(note), "AUTO");
        }

        // Deadline reminders
        if (containsKeywords(lowerContent, "deadline", "due", "submit", "delivery")) {
            createReminder(note, baseTime.plusDays(7),
                    "Deadline approaching: " + getTitleOrDefault(note), "AUTO");
            createReminder(note, baseTime.plusDays(1),
                    "Final deadline reminder: " + getTitleOrDefault(note), "AUTO");
        }

        // Follow-up reminders
        if (containsKeywords(lowerContent, "follow up", "follow-up", "check back", "revisit")) {
            createReminder(note, baseTime.plusDays(3),
                    "Follow-up reminder: " + getTitleOrDefault(note), "AUTO");
        }

        // Weekly reminders for recurring tasks
        if (containsKeywords(lowerContent, "weekly", "every week", "recurring")) {
            createReminder(note, baseTime.plusWeeks(1),
                    "Weekly reminder: " + getTitleOrDefault(note), "RECURRING");
        }
    }

    private boolean containsKeywords(String content, String... keywords) {
        for (String keyword : keywords) {
            if (content.contains(keyword)) {
                return true;
            }
        }
        return false;
    }

    private String getTitleOrDefault(Note note) {
        return note.getTitle() != null && !note.getTitle().trim().isEmpty()
                ? note.getTitle() : "Untitled Note";
    }

    private void createReminder(Note note, LocalDateTime reminderTime, String message, String type) {
        Reminder reminder = new Reminder();
        reminder.setNote(note);
        reminder.setReminderTime(reminderTime);
        reminder.setMessage(message);
        reminder.setReminderType(type);
        reminder.setIsSent(false);
        reminder.setIsActive(true);

        reminderRepository.save(reminder);
    }

    // Scheduled method to process pending reminders
    @Scheduled(fixedDelay = 300000) // Run every 5 minutes
    public void processPendingReminders() {
        try {
            LocalDateTime now = LocalDateTime.now();
            List<Reminder> pendingReminders = reminderRepository.findByIsSentFalseAndReminderTimeBefore(now);

            for (Reminder reminder : pendingReminders) {
                try {
                    sendReminder(reminder);

                    // Mark as sent
                    reminder.setIsSent(true);
                    reminder.setSentAt(now);
                    reminder.setUpdatedAt(now);
                    reminderRepository.save(reminder);

                } catch (Exception e) {
                    System.err.println("Failed to send reminder " + reminder.getReminderId() + ": " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing pending reminders: " + e.getMessage());
        }
    }

    // Send reminder (implement based on your notification system)
    private void sendReminder(Reminder reminder) {
        try {
            // Send real-time popup notification
            notificationService.sendReminderNotification(reminder);

            // Optional: Send additional notifications
            // notificationService.sendEmailNotification(reminder);
            // notificationService.sendSMSNotification(reminder);
            // notificationService.sendPushNotification(reminder);

        } catch (Exception e) {
            System.err.println("Failed to send reminder notification: " + e.getMessage());

            // Fallback to console notification
            System.out.println("=== REMINDER NOTIFICATION (FALLBACK) ===");
            System.out.println("User: " + reminder.getNote().getUser().getUsername());
            System.out.println("Note: " + getTitleOrDefault(reminder.getNote()));
            System.out.println("Message: " + reminder.getMessage());
            System.out.println("Reminder Time: " + reminder.getReminderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("=======================================");
        }
    }

    // Additional utility methods
    public List<Reminder> getOverdueReminders(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        return reminderRepository.findByNoteUserUserIdAndIsSentFalseAndReminderTimeBeforeOrderByReminderTimeAsc(
                currentUser.getUserId(), oneHourAgo);
    }

    public List<Reminder> getActiveReminders(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        return reminderRepository.findByNoteUserUserIdOrderByReminderTimeDesc(currentUser.getUserId())
                .stream()
                .filter(r -> r.getIsActive())
                .toList();
    }

    public Reminder snoozeReminder(Integer id, String token, int minutes) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Reminder reminder = reminderRepository.findByReminderIdAndNoteUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Reminder not found or access denied"));

        reminder.setReminderTime(reminder.getReminderTime().plusMinutes(minutes));
        reminder.setIsSent(false);
        reminder.setUpdatedAt(LocalDateTime.now());

        return reminderRepository.save(reminder);
    }

    // Legacy method for backward compatibility
    public List<Reminder> findByreminderTimeBefore(LocalDateTime time) {
        return reminderRepository.findByreminderTimeBefore(time);
    }
}