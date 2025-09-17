package com.techm.controller;

import com.techm.model.Flashcard;
import com.techm.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FlashcardController {

    @Autowired
    private FlashcardService flashcardService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Flashcard>> getFlashcardsByNote(@PathVariable Integer noteId,
                                                               @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Flashcard> flashcards = flashcardService.getAllFlashcardsByNote(noteId, token);
            return ResponseEntity.ok(flashcards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/due")
    public ResponseEntity<List<Flashcard>> getDueFlashcards(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Flashcard> flashcards = flashcardService.getDueFlashcards(token);
            return ResponseEntity.ok(flashcards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/note/{noteId}")
    public ResponseEntity<Flashcard> createFlashcard(@PathVariable Integer noteId,
                                                     @RequestBody Flashcard flashcard,
                                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Flashcard savedFlashcard = flashcardService.createFlashcard(noteId, flashcard, token);
            return ResponseEntity.ok(savedFlashcard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/review")
    public ResponseEntity<Flashcard> reviewFlashcard(@PathVariable Integer id,
                                                     @RequestParam Integer score,
                                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Flashcard flashcard = flashcardService.reviewFlashcard(id, score, token);
            return ResponseEntity.ok(flashcard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Flashcard> updateFlashcard(@PathVariable Integer id,
                                                     @RequestBody Flashcard flashcard,
                                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Flashcard updatedFlashcard = flashcardService.updateFlashcard(id, flashcard, token);
            return ResponseEntity.ok(updatedFlashcard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFlashcard(@PathVariable Integer id,
                                             @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            flashcardService.deleteFlashcard(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Legacy endpoints for compatibility
    @GetMapping
    public List<Flashcard> getAll() {
        return flashcardService.getAll();
    }

    @GetMapping("/{id}")
    public Flashcard getById(@PathVariable Integer id) {
        return flashcardService.getById(id);
    }

    @PostMapping
    public Flashcard create(@RequestBody Flashcard obj) {
        return flashcardService.save(obj);
    }
}