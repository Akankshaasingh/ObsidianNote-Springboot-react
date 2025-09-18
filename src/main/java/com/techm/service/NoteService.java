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

    // FIXED: Flexible method to get current user - works with or without token
    private User getCurrentUser(String token) {
        System.out.println("=== USER AUTHENTICATION DEBUG ===");
        System.out.println("Received token: " + (token != null ? token : "null"));

        // If token is provided, try token-based authentication
        if (token != null && token.startsWith("simple_")) {
            try {
                String[] parts = token.split("_");
                if (parts.length >= 3) {
                    Integer userId = Integer.parseInt(parts[1]);
                    Optional<User> userOpt = userRepository.findById(userId); // FIXED: Added <User>
                    if (userOpt.isPresent()) {
                        System.out.println("Token authentication successful for user: " + userOpt.get().getUsername());
                        return userOpt.get();
                    }
                }
            } catch (Exception e) {
                System.out.println("Token authentication failed: " + e.getMessage());
            }
        }

        // Fallback: Use first available user for development
        List<User> allUsers = userRepository.findAll();
        if (!allUsers.isEmpty()) {
            User defaultUser = allUsers.get(0);
            System.out.println("Using fallback user: " + defaultUser.getUsername() + " (ID: " + defaultUser.getUserId() + ")");
            return defaultUser;
        }

        // FIXED: If no users exist, create a default one (matches your specification)
        User defaultUser = new User();
        defaultUser.setUsername("testuser");          // CHANGED from "defaultuser"
        defaultUser.setEmail("test@example.com");     // CHANGED from "default@example.com"
        defaultUser.setPasswordHash("testhash");      // CHANGED from "defaulthash"
        defaultUser.setThemePreference("dark");
        defaultUser.setLastLogin(LocalDateTime.now());

        User savedUser = userRepository.save(defaultUser);
        System.out.println("Created default user: " + savedUser.getUsername() + " (ID: " + savedUser.getUserId() + ")");

        return savedUser;
    }

    public List<Note> getAllNotes(String token) {
        System.out.println("getAllNotes called");

        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
        }

        List<Note> notes = noteRepository.findByUserUserIdOrderByUpdatedAtDesc(currentUser.getUserId());
        System.out.println("Found " + notes.size() + " notes for user: " + currentUser.getUsername());

        return notes;
    }

    public Note getNoteById(Integer id, String token) {
        System.out.println("getNoteById called for ID: " + id);

        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
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
        System.out.println("saveNote called");
        System.out.println("Note data - Title: " + note.getTitle() + ", Content length: " +
                (note.getContent() != null ? note.getContent().length() : 0));

        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
        }

        LocalDateTime now = LocalDateTime.now();
        boolean isNewNote = note.getNoteId() == null;
        System.out.println("Is new note: " + isNewNote);

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
            System.out.println("Creating new note for user: " + currentUser.getUsername());
        } else {
            // Updating existing note
            Note existingNote = noteRepository.findByNoteIdAndUserUserId(note.getNoteId(), currentUser.getUserId())
                    .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

            note.setUser(currentUser);
            note.setCreatedAt(existingNote.getCreatedAt());
            note.setUpdatedAt(now);
            note.setLastAccessed(now);
            System.out.println("Updating existing note: " + note.getNoteId());
        }

        Note savedNote = noteRepository.save(note);
        System.out.println("Note saved successfully with ID: " + savedNote.getNoteId());

        // Auto-create reminders for new notes
        if (isNewNote) {
            try {
                reminderService.createAutoReminders(savedNote);
                System.out.println("Auto-reminders created successfully");
            } catch (Exception e) {
                System.err.println("Failed to create auto reminders: " + e.getMessage());
            }
        }

        return savedNote;
    }

    public void deleteNote(Integer id, String token) {
        System.out.println("deleteNote called for ID: " + id);

        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
        }

        Note note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        noteRepository.delete(note);
        System.out.println("Note deleted successfully");
    }

    public List<Note> getStarredNotes(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
        }
        return noteRepository.findByUserUserIdAndIsStarredTrueOrderByUpdatedAtDesc(currentUser.getUserId());
    }

    public List<Note> searchNotes(String searchTerm, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
        }
        return noteRepository.searchNotesByUserAndTerm(currentUser.getUserId(), searchTerm);
    }

    public Note toggleStar(Integer id, String token) {
        System.out.println("toggleStar called for ID: " + id);

        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("Unable to determine user");
        }

        Note note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        note.setIsStarred(!note.getIsStarred());
        note.setUpdatedAt(LocalDateTime.now());

        Note updatedNote = noteRepository.save(note);
        System.out.println("Note star toggled to: " + updatedNote.getIsStarred());

        return updatedNote;
    }

    // Additional methods for development/testing without token requirement

    public List<Note> getAllNotesForDevelopment() {
        System.out.println("Development mode: getAllNotesForDevelopment called");
        return noteRepository.findAll();
    }

    public Note saveNoteForDevelopment(Note note) {
        System.out.println("Development mode: saveNoteForDevelopment called");

        // Get or create default user
        User defaultUser = getCurrentUser(null);

        LocalDateTime now = LocalDateTime.now();
        boolean isNewNote = note.getNoteId() == null;

        if (isNewNote) {
            note.setUser(defaultUser);
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
            note.setUpdatedAt(now);
            note.setLastAccessed(now);
        }

        return noteRepository.save(note);
    }
}





//package com.techm.service;
//
//import com.techm.model.Note;
//import com.techm.model.User;
//import com.techm.repository.NoteRepository;
//import com.techm.repository.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class NoteService {
//
//    @Autowired
//    private NoteRepository noteRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    @Autowired
//    private ReminderService reminderService;
//
//    // Simple method to get current user from token
//    private User getCurrentUser(String token) {
//        if (token != null && token.startsWith("simple_")) {
//            try {
//                String[] parts = token.split("_");
//                if (parts.length >= 3) {
//                    Integer userId = Integer.parseInt(parts[1]);
//                    return userRepository.findById(userId).orElse(null);
//                }
//            } catch (Exception e) {
//                return null;
//            }
//        }
//        return null;
//    }
//
//    public List<Note> getAllNotes(String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//        return noteRepository.findByUserUserIdOrderByUpdatedAtDesc(currentUser.getUserId());
//    }
//
//    public Note getNoteById(Integer id, String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//
//        Optional<Note> note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId());
//        if (note.isPresent()) {
//            Note foundNote = note.get();
//            foundNote.setLastAccessed(LocalDateTime.now());
//            return noteRepository.save(foundNote);
//        }
//        return null;
//    }
//
//    public Note saveNote(Note note, String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//
//        LocalDateTime now = LocalDateTime.now();
//        boolean isNewNote = note.getNoteId() == null;
//
//        if (isNewNote) {
//            // New note
//            note.setUser(currentUser);
//            note.setCreatedAt(now);
//            note.setUpdatedAt(now);
//            note.setLastAccessed(now);
//            if (note.getIsStarred() == null) {
//                note.setIsStarred(false);
//            }
//            if (note.getIsEncrypted() == null) {
//                note.setIsEncrypted(false);
//            }
//        } else {
//            // Updating existing note
//            Note existingNote = noteRepository.findByNoteIdAndUserUserId(note.getNoteId(), currentUser.getUserId())
//                    .orElseThrow(() -> new RuntimeException("Note not found or access denied"));
//
//            note.setUser(currentUser);
//            note.setCreatedAt(existingNote.getCreatedAt());
//            note.setUpdatedAt(now);
//            note.setLastAccessed(now);
//        }
//
//        Note savedNote = noteRepository.save(note);
//
//        // Auto-create reminders for new notes
//        if (isNewNote) {
//            try {
//                reminderService.createAutoReminders(savedNote);
//            } catch (Exception e) {
//                System.err.println("Failed to create auto reminders: " + e.getMessage());
//            }
//        }
//
//        return savedNote;
//    }
//
//    public void deleteNote(Integer id, String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//
//        Note note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId())
//                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));
//
//        noteRepository.delete(note);
//    }
//
//    public List<Note> getStarredNotes(String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//        return noteRepository.findByUserUserIdAndIsStarredTrueOrderByUpdatedAtDesc(currentUser.getUserId());
//    }
//
//    public List<Note> searchNotes(String searchTerm, String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//        return noteRepository.searchNotesByUserAndTerm(currentUser.getUserId(), searchTerm);
//    }
//
//    public Note toggleStar(Integer id, String token) {
//        User currentUser = getCurrentUser(token);
//        if (currentUser == null) {
//            throw new RuntimeException("User not authenticated");
//        }
//
//        Note note = noteRepository.findByNoteIdAndUserUserId(id, currentUser.getUserId())
//                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));
//
//        note.setIsStarred(!note.getIsStarred());
//        note.setUpdatedAt(LocalDateTime.now());
//
//        return noteRepository.save(note);
//    }
//}