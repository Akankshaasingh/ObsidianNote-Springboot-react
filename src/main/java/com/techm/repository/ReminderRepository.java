package com.techm.repository;

import com.techm.model.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    // Find reminders by time - existing method (keep for compatibility)
    List<Reminder> findByreminderTimeBefore(LocalDateTime time);

    // New methods for enhanced functionality
    List<Reminder> findByIsSentFalseAndReminderTimeBefore(LocalDateTime time);

    @Query("SELECT r FROM Reminder r WHERE r.note.user.userId = :userId ORDER BY r.reminderTime DESC")
    List<Reminder> findByNoteUserUserIdOrderByReminderTimeDesc(@Param("userId") Integer userId);

    @Query("SELECT r FROM Reminder r WHERE r.reminderId = :reminderId AND r.note.user.userId = :userId")
    Optional<Reminder> findByReminderIdAndNoteUserUserId(@Param("reminderId") Integer reminderId,
                                                         @Param("userId") Integer userId);

    @Query("SELECT r FROM Reminder r WHERE r.note.user.userId = :userId AND r.isSent = false AND r.reminderTime <= :currentTime ORDER BY r.reminderTime ASC")
    List<Reminder> findByNoteUserUserIdAndIsSentFalseAndReminderTimeBeforeOrderByReminderTimeAsc(
            @Param("userId") Integer userId,
            @Param("currentTime") LocalDateTime currentTime);
}