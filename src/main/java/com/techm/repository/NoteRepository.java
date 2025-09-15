package com.techm.repository;

import com.techm.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

    List<Note> findByUserUserIdOrderByUpdatedAtDesc(Integer userId);

    List<Note> findByUserUserIdAndIsStarredTrueOrderByUpdatedAtDesc(Integer userId);

    @Query("SELECT n FROM Note n WHERE n.user.userId = :userId AND " +
            "(LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(n.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Note> searchNotesByUserAndTerm(@Param("userId") Integer userId,
                                        @Param("searchTerm") String searchTerm);

    Optional<Note> findByNoteIdAndUserUserId(Integer noteId, Integer userId);

    @Query("SELECT COUNT(n) FROM Note n WHERE n.user.userId = :userId")
    long countByUserId(@Param("userId") Integer userId);
}