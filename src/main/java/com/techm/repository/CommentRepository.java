package com.techm.repository;

import com.techm.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Integer> {

    List<Comment> findByNoteNoteIdOrderByCreatedAtDesc(Integer noteId);

    @Query("SELECT c FROM Comment c WHERE c.commentId = :commentId AND c.user.userId = :userId")
    Optional<Comment> findByCommentIdAndUserUserId(@Param("commentId") Integer commentId, @Param("userId") Integer userId);

    @Query("SELECT c FROM Comment c WHERE c.note.user.userId = :userId ORDER BY c.createdAt DESC")
    List<Comment> findByNoteUserUserIdOrderByCreatedAtDesc(@Param("userId") Integer userId);
}
