package com.techm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Reminder")
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reminderId;

    @ManyToOne
    @JoinColumn(name = "note_id")
    private Note note;

    private LocalDateTime reminderTime;

    @Column(columnDefinition = "TEXT")
    private String message;

    private Boolean isSent = false;

	public Integer getReminderId() {
		return reminderId;
	}

	public void setReminderId(Integer reminderId) {
		this.reminderId = reminderId;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

	public LocalDateTime getReminderTime() {
		return reminderTime;
	}

	public void setReminderTime(LocalDateTime reminderTime) {
		this.reminderTime = reminderTime;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean getIsSent() {
		return isSent;
	}

	public void setIsSent(Boolean isSent) {
		this.isSent = isSent;
	}

   
}
