package com.techm.service;

import com.techm.model.Note;
import com.techm.model.User;
import com.techm.repository.NoteRepository;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReminderService reminderService;

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

    public List<Note> getAllNotes(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        return noteRepository.findByUserUserIdOrderByUpdatedAtDesc(currentUser.getUserId());
    }

    public Note getNoteById(Integer id, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Optional<Note> note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId());
        if (note.isPresent()) {
            Note foundNote = note.get();
            foundNote.setLastAccessed(LocalDateTime.now());
            return noteRepository.save(foundNote);
        }
        return null;
    }

    public Note saveNote(Note note, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isNewNote = note.getNoteId() == null;

        if (isNewNote) {
            // New note
            note.setUser(currentUser);
            note.setCreatedAt(now);
            note.setUpdatedAt(now);
            note.setLastAccessed(now);
            if (note.getIsStarred() == null) {
                note.setIsStarred(false);
            }
            if (note.getIsEncrypted() == null) {
                note.setIsEncrypted(false);
            }
        } else {
            // Updating existing note
            Note existingNote = noteRepository.findByNoteIdAndUserUserId(note.getNoteId(), currentUser.getUserId())
                    .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

            note.setUser(currentUser);
            note.setCreatedAt(existingNote.getCreatedAt());
            note.setUpdatedAt(now);
            note.setLastAccessed(now);
        }

        Note savedNote = noteRepository.save(note);

        // Auto-create reminders for new notes
        if (isNewNote) {
            try {
                reminderService.createAutoReminders(savedNote);
            } catch (Exception e) {
                System.err.println("Failed to create auto reminders: " + e.getMessage());
            }
        }

        return savedNote;
    }

    public void deleteNote(Integer id, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Note note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        noteRepository.delete(note);
    }

    public List<Note> getStarredNotes(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        return noteRepository.findByUserUserIdAndIsStarredTrueOrderByUpdatedAtDesc(currentUser.getUserId());
    }

    public List<Note> searchNotes(String searchTerm, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }
        return noteRepository.searchNotesByUserAndTerm(currentUser.getUserId(), searchTerm);
    }

    public Note toggleStar(Integer id, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Note note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        note.setIsStarred(!note.getIsStarred());
        note.setUpdatedAt(LocalDateTime.now());

        return noteRepository.save(note);
    }
}