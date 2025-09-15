package com.techm.controller;

import com.techm.model.Note;
import com.techm.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NoteController {

    @Autowired
    private NoteService noteService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Note> notes = noteService.getAllNotes(token);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Integer id,
                                            @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Note note = noteService.getNoteById(id, token);
            if (note != null) {
                return ResponseEntity.ok(note);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Note savedNote = noteService.saveNote(note, token);
            return ResponseEntity.ok(savedNote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Integer id,
                                           @RequestBody Note note,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            note.setNoteId(id);
            Note updatedNote = noteService.saveNote(note, token);
            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Integer id,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            noteService.deleteNote(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/starred")
    public ResponseEntity<List<Note>> getStarredNotes(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Note> notes = noteService.getStarredNotes(token);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String q,
                                                  @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Note> notes = noteService.searchNotes(q, token);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/star")
    public ResponseEntity<Note> toggleStar(@PathVariable Integer id,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Note note = noteService.toggleStar(id, token);
            return ResponseEntity.ok(note);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}