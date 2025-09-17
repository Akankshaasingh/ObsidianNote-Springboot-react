// Enhanced CommentController.java
package com.techm.controller;

import com.techm.model.Comment;
import com.techm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CommentController {

    @Autowired
    private CommentService commentService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Comment>> getCommentsByNote(@PathVariable Integer noteId,
                                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Comment> comments = commentService.getAllCommentsByNote(noteId, token);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/note/{noteId}")
    public ResponseEntity<Comment> createComment(@PathVariable Integer noteId,
                                                 @RequestBody Comment comment,
                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Comment savedComment = commentService.createComment(noteId, comment, token);
            return ResponseEntity.ok(savedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Comment> updateComment(@PathVariable Integer id,
                                                 @RequestBody Comment comment,
                                                 @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Comment updatedComment = commentService.updateComment(id, comment, token);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer id,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            commentService.deleteComment(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Legacy endpoints for compatibility
    @GetMapping
    public List<Comment> getAll() {
        return commentService.getAll();
    }

    @GetMapping("/{id}")
    public Comment getById(@PathVariable Integer id) {
        return commentService.getById(id);
    }

    @PostMapping
    public Comment create(@RequestBody Comment obj) {
        return commentService.save(obj);
    }

    @PutMapping("/legacy/{id}")
    public Comment update(@PathVariable Integer id, @RequestBody Comment obj) {
        obj.setCommentId(id);
        return commentService.save(obj);
    }

    @DeleteMapping("/legacy/{id}")
    public void delete(@PathVariable Integer id) {
        commentService.delete(id);
    }
}