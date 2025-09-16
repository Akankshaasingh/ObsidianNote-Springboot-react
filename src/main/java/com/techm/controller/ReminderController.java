package com.techm.controller;

import com.techm.model.Reminder;
import com.techm.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReminderController {

    @Autowired
    private ReminderService reminderService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Reminder>> getAllReminders(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Reminder> reminders = reminderService.getAllRemindersByUser(token);
            return ResponseEntity.ok(reminders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Reminder> getReminderById(@PathVariable Integer id,
                                                    @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Reminder reminder = reminderService.getReminderById(id, token);
            if (reminder != null) {
                return ResponseEntity.ok(reminder);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Reminder> createReminder(@RequestBody Reminder reminder,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Reminder savedReminder = reminderService.saveReminder(reminder, token);
            return ResponseEntity.ok(savedReminder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Reminder> updateReminder(@PathVariable Integer id,
                                                   @RequestBody Reminder reminder,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            reminder.setReminderId(id);
            Reminder updatedReminder = reminderService.saveReminder(reminder, token);
            return ResponseEntity.ok(updatedReminder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReminder(@PathVariable Integer id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            reminderService.deleteReminder(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Reminder>> getPendingReminders(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Reminder> pendingReminders = reminderService.getPendingReminders(token);
            return ResponseEntity.ok(pendingReminders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/mark-sent")
    public ResponseEntity<Reminder> markReminderAsSent(@PathVariable Integer id,
                                                       @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Reminder reminder = reminderService.markAsSent(id, token);
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}