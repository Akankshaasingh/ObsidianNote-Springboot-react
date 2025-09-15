package com.techm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Note")
public class Note {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Integer noteId;
	// Change this line in Note.java:
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer noteId;  // Keep as Integer, not Long

	// Make sure NoteRepository uses Integer:

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    private Boolean isStarred = false;
    private Boolean isEncrypted = false;
    private LocalDateTime lastAccessed;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    private List<Reminder> reminders;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    private List<Flashcard> flashcards;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    private List<Comment> comments;

    public Integer getNoteId() {
		return noteId;
	}

	public void setNoteId(Integer noteId) {
		this.noteId = noteId;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Boolean getIsStarred() {
		return isStarred;
	}

	public void setIsStarred(Boolean isStarred) {
		this.isStarred = isStarred;
	}

	public Boolean getIsEncrypted() {
		return isEncrypted;
	}

	public void setIsEncrypted(Boolean isEncrypted) {
		this.isEncrypted = isEncrypted;
	}

	public LocalDateTime getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(LocalDateTime lastAccessed) {
		this.lastAccessed = lastAccessed;
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

	public List<Reminder> getReminders() {
		return reminders;
	}

	public void setReminders(List<Reminder> reminders) {
		this.reminders = reminders;
	}

	public List<Flashcard> getFlashcards() {
		return flashcards;
	}

	public void setFlashcards(List<Flashcard> flashcards) {
		this.flashcards = flashcards;
	}

	public List<Task> getTasks() {
		return tasks;
	}

	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public List<NoteLink> getOutgoingLinks() {
		return outgoingLinks;
	}

	public void setOutgoingLinks(List<NoteLink> outgoingLinks) {
		this.outgoingLinks = outgoingLinks;
	}

	public List<NoteLink> getIncomingLinks() {
		return incomingLinks;
	}

	public void setIncomingLinks(List<NoteLink> incomingLinks) {
		this.incomingLinks = incomingLinks;
	}

	@OneToMany(mappedBy = "sourceNote", cascade = CascadeType.ALL)
    private List<NoteLink> outgoingLinks;

    @OneToMany(mappedBy = "targetNote", cascade = CascadeType.ALL)
    private List<NoteLink> incomingLinks;

   
}
