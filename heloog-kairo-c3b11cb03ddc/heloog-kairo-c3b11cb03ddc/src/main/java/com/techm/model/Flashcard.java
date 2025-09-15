package com.techm.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Flashcard")
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flashcardId;

    @ManyToOne
    @JoinColumn(name = "note_id")
    private Note note;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    public Integer getFlashcardId() {
		return flashcardId;
	}
	public void setFlashcardId(Integer flashcardId) {
		this.flashcardId = flashcardId;
	}
	public Note getNote() {
		return note;
	}
	public void setNote(Note note) {
		this.note = note;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getAnswer() {
		return answer;
	}
	public void setAnswer(String answer) {
		this.answer = answer;
	}
	public Integer getReviewScore() {
		return reviewScore;
	}
	public void setReviewScore(Integer reviewScore) {
		this.reviewScore = reviewScore;
	}
	public LocalDateTime getLastReviewed() {
		return lastReviewed;
	}
	public void setLastReviewed(LocalDateTime lastReviewed) {
		this.lastReviewed = lastReviewed;
	}
	public LocalDateTime getNextReviewDate() {
		return nextReviewDate;
	}
	public void setNextReviewDate(LocalDateTime nextReviewDate) {
		this.nextReviewDate = nextReviewDate;
	}
	private Integer reviewScore;
    private LocalDateTime lastReviewed;
    private LocalDateTime nextReviewDate;

   
}
