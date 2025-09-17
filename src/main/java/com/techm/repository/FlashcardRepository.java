package com.techm.repository;

import com.techm.model.Flashcard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlashcardRepository extends JpaRepository<Flashcard, Integer> {

    List<Flashcard> findByNoteNoteIdOrderByLastReviewedAsc(Integer noteId);

    List<Flashcard> findByNoteUserUserIdAndNextReviewDateBeforeOrderByNextReviewDateAsc(
            Integer userId, LocalDateTime date);

    List<Flashcard> findByNoteUserUserIdOrderByNextReviewDateAsc(Integer userId);

    @Query("SELECT f FROM Flashcard f WHERE f.flashcardId = :flashcardId AND f.note.user.userId = :userId")
    Optional<Flashcard> findByFlashcardIdAndNoteUserUserId(@Param("flashcardId") Integer flashcardId, @Param("userId") Integer userId);
}