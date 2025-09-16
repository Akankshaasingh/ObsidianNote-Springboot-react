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

	private Boolean isActive = true;

	private String reminderType;

	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;

	private LocalDateTime sentAt;

	// Constructors
	public Reminder() {
	}

	public Reminder(Note note, LocalDateTime reminderTime, String message) {
		this.note = note;
		this.reminderTime = reminderTime;
		this.message = message;
		this.isSent = false;
		this.isActive = true;
		this.createdAt = LocalDateTime.now();
		this.updatedAt = LocalDateTime.now();
	}

	// Getters and Setters
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

	public Boolean getIsActive() {
		return isActive;
	}

	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	public String getReminderType() {
		return reminderType;
	}

	public void setReminderType(String reminderType) {
		this.reminderType = reminderType;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public void setSentAt(LocalDateTime sentAt) {
		this.sentAt = sentAt;
	}

	// Utility methods
	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
		updatedAt = LocalDateTime.now();
		if (isSent == null) {
			isSent = false;
		}
		if (isActive == null) {
			isActive = true;
		}
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	@Override
	public String toString() {
		return "Reminder{" +
				"reminderId=" + reminderId +
				", reminderTime=" + reminderTime +
				", message='" + message + '\'' +
				", isSent=" + isSent +
				", isActive=" + isActive +
				", reminderType='" + reminderType + '\'' +
				", createdAt=" + createdAt +
				", updatedAt=" + updatedAt +
				", sentAt=" + sentAt +
				'}';
	}
}