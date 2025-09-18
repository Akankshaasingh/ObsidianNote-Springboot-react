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
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            System.out.println("getAllNotes called with auth header: " + (authHeader != null ? "present" : "null"));

            List<Note> notes = noteService.getAllNotes(token);
            System.out.println("Returning " + notes.size() + " notes");

            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            System.err.println("Error in getAllNotes: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Note> getNoteById(@PathVariable Integer id,
                                            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            System.out.println("getNoteById called for ID: " + id);

            Note note = noteService.getNoteById(id, token);
            if (note != null) {
                return ResponseEntity.ok(note);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Error in getNoteById: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            System.out.println("createNote called");
            System.out.println("Request body - Title: " + note.getTitle() + ", Content: " +
                    (note.getContent() != null ? note.getContent().substring(0, Math.min(50, note.getContent().length())) : "null"));

            Note savedNote = noteService.saveNote(note, token);
            System.out.println("Note created successfully with ID: " + savedNote.getNoteId());

            return ResponseEntity.ok(savedNote);
        } catch (Exception e) {
            System.err.println("Error in createNote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Note> updateNote(@PathVariable Integer id,
                                           @RequestBody Note note,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            System.out.println("updateNote called for ID: " + id);

            note.setNoteId(id);
            Note updatedNote = noteService.saveNote(note, token);
            System.out.println("Note updated successfully");

            return ResponseEntity.ok(updatedNote);
        } catch (Exception e) {
            System.err.println("Error in updateNote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNote(@PathVariable Integer id,
                                        @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            System.out.println("deleteNote called for ID: " + id);

            noteService.deleteNote(id, token);
            System.out.println("Note deleted successfully");

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            System.err.println("Error in deleteNote: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/starred")
    public ResponseEntity<List<Note>> getStarredNotes(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Note> notes = noteService.getStarredNotes(token);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            System.err.println("Error in getStarredNotes: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<Note>> searchNotes(@RequestParam String q,
                                                  @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Note> notes = noteService.searchNotes(q, token);
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            System.err.println("Error in searchNotes: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/star")
    public ResponseEntity<Note> toggleStar(@PathVariable Integer id,
                                           @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            String token = extractToken(authHeader);
            System.out.println("toggleStar called for ID: " + id);

            Note note = noteService.toggleStar(id, token);
            System.out.println("Star toggled successfully");

            return ResponseEntity.ok(note);
        } catch (Exception e) {
            System.err.println("Error in toggleStar: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    // Development endpoints without token requirement
    @GetMapping("/dev/all")
    public ResponseEntity<List<Note>> getAllNotesForDev() {
        try {
            System.out.println("Development endpoint: getAllNotesForDev called");
            List<Note> notes = noteService.getAllNotesForDevelopment();
            return ResponseEntity.ok(notes);
        } catch (Exception e) {
            System.err.println("Error in dev getAllNotes: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/dev/create")
    public ResponseEntity<Note> createNoteForDev(@RequestBody Note note) {
        try {
            System.out.println("Development endpoint: createNoteForDev called");
            Note savedNote = noteService.saveNoteForDevelopment(note);
            return ResponseEntity.ok(savedNote);
        } catch (Exception e) {
            System.err.println("Error in dev createNote: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}




//package com.techm.controller;
//
//import com.techm.model.Note;
//import com.techm.service.NoteService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/notes")
//@CrossOrigin(origins = "*", maxAge = 3600)
//public class NoteController {
//
//    @Autowired
//    private NoteService noteService;
//
//    private String extractToken(String authHeader) {
//        if (authHeader != null && authHeader.startsWith("Bearer ")) {
//            return authHeader.substring(7);
//        }
//        return null;
//    }
//
//    @GetMapping
//    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            List<Note> notes = noteService.getAllNotes(token);
//            return ResponseEntity.ok(notes);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Note> getNoteById(@PathVariable Integer id,
//                                            @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            Note note = noteService.getNoteById(id, token);
//            if (note != null) {
//                return ResponseEntity.ok(note);
//            }
//            return ResponseEntity.notFound().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping
//    public ResponseEntity<Note> createNote(@RequestBody Note note,
//                                           @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            Note savedNote = noteService.saveNote(note, token);
//            return ResponseEntity.ok(savedNote);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<Note> updateNote(@PathVariable Integer id,
//                                           @RequestBody Note note,
//                                           @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            note.setNoteId(id);
//            Note updatedNote = noteService.saveNote(note, token);
//            return ResponseEntity.ok(updatedNote);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteNote(@PathVariable Integer id,
//                                        @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            noteService.deleteNote(id, token);
//            return ResponseEntity.ok().build();
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping("/starred")
//    public ResponseEntity<List<Note>> getStarredNotes(@RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            List<Note> notes = noteService.getStarredNotes(token);
//            return ResponseEntity.ok(notes);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<List<Note>> searchNotes(@RequestParam String q,
//                                                  @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            List<Note> notes = noteService.searchNotes(q, token);
//            return ResponseEntity.ok(notes);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//
//    @PostMapping("/{id}/star")
//    public ResponseEntity<Note> toggleStar(@PathVariable Integer id,
//                                           @RequestHeader("Authorization") String authHeader) {
//        try {
//            String token = extractToken(authHeader);
//            Note note = noteService.toggleStar(id, token);
//            return ResponseEntity.ok(note);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest().build();
//        }
//    }
//}