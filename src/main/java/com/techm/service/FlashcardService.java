package com.techm.service;

import com.techm.model.Flashcard;
import com.techm.model.Note;
import com.techm.model.User;
import com.techm.repository.FlashcardRepository;
import com.techm.repository.NoteRepository;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FlashcardService {

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    // Simple method to get current user from token
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

    public List<Flashcard> getAllFlashcardsByNote(Integer noteId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify note belongs to user
        Note note = noteRepository.findByNoteIdAndUserUserId(noteId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        return flashcardRepository.findByNoteNoteIdOrderByLastReviewedAsc(noteId);
    }

    public List<Flashcard> getDueFlashcards(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        LocalDateTime now = LocalDateTime.now();
        return flashcardRepository.findByNoteUserUserIdAndNextReviewDateBeforeOrderByNextReviewDateAsc(
                currentUser.getUserId(), now);
    }

    public Flashcard createFlashcard(Integer noteId, Flashcard flashcard, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify note belongs to user
        Note note = noteRepository.findByNoteIdAndUserUserId(noteId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        flashcard.setNote(note);
        flashcard.setReviewScore(0);
        flashcard.setLastReviewed(LocalDateTime.now());
        flashcard.setNextReviewDate(LocalDateTime.now().plusDays(1)); // Next review in 1 day

        return flashcardRepository.save(flashcard);
    }

    public Flashcard reviewFlashcard(Integer flashcardId, Integer score, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Flashcard flashcard = flashcardRepository.findByFlashcardIdAndNoteUserUserId(flashcardId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Flashcard not found or access denied"));

        flashcard.setReviewScore(score);
        flashcard.setLastReviewed(LocalDateTime.now());

        // Simple spaced repetition algorithm
        int days = switch (score) {
            case 1 -> 1; // Again
            case 2 -> 3; // Hard
            case 3 -> 7; // Good
            case 4 -> 14; // Easy
            default -> 1;
        };

        flashcard.setNextReviewDate(LocalDateTime.now().plusDays(days));
        return flashcardRepository.save(flashcard);
    }

    public Flashcard updateFlashcard(Integer flashcardId, Flashcard flashcardData, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Flashcard flashcard = flashcardRepository.findByFlashcardIdAndNoteUserUserId(flashcardId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Flashcard not found or access denied"));

        flashcard.setQuestion(flashcardData.getQuestion());
        flashcard.setAnswer(flashcardData.getAnswer());

        return flashcardRepository.save(flashcard);
    }

    public void deleteFlashcard(Integer flashcardId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Flashcard flashcard = (Flashcard) flashcardRepository.findByFlashcardIdAndNoteUserUserId(flashcardId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Flashcard not found or access denied"));

        flashcardRepository.delete(flashcard);
    }

    // Legacy methods for compatibility
    public List<Flashcard> getAll() {
        return flashcardRepository.findAll();
    }

    public Flashcard getById(Integer id) {
        return flashcardRepository.findById(id).orElse(null);
    }

    public Flashcard save(Flashcard obj) {
        return flashcardRepository.save(obj);
    }

    public void delete(Integer id) {
        flashcardRepository.deleteById(id);
    }
}