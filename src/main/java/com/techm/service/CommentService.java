package com.techm.service;

import com.techm.model.Comment;
import com.techm.model.Note;
import com.techm.model.User;
import com.techm.repository.CommentRepository;
import com.techm.repository.NoteRepository;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

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

    public List<Comment> getAllCommentsByNote(Integer noteId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify note belongs to user
        Note note = noteRepository.findByNoteIdAndUserUserId(noteId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        return commentRepository.findByNoteNoteIdOrderByCreatedAtDesc(noteId);
    }

    public Comment createComment(Integer noteId, Comment comment, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify note belongs to user
        Note note = noteRepository.findByNoteIdAndUserUserId(noteId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        comment.setNote(note);
        comment.setUser(currentUser);
        comment.setCreatedAt(LocalDateTime.now());

        return commentRepository.save(comment);
    }

    public Comment updateComment(Integer commentId, Comment commentData, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Comment comment = (Comment) commentRepository.findByCommentIdAndUserUserId(commentId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Comment not found or access denied"));

        comment.setCommentText(commentData.getCommentText());
        return commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Comment comment = (Comment) commentRepository.findByCommentIdAndUserUserId(commentId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Comment not found or access denied"));

        commentRepository.delete(comment);
    }

    // Legacy methods for compatibility
    public List<Comment> getAll() {
        return commentRepository.findAll();
    }

    public Comment getById(Integer id) {
        return commentRepository.findById(id).orElse(null);
    }

    public Comment save(Comment obj) {
        return commentRepository.save(obj);
    }

    public void delete(Integer id) {
        commentRepository.deleteById(id);
    }
}