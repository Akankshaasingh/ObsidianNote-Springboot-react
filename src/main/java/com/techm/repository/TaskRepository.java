// Enhanced TaskRepository.java
package com.techm.repository;

import com.techm.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findByNoteNoteIdOrderByDueDateAsc(Integer noteId);

    List<Task> findByNoteUserUserIdOrderByDueDateAsc(Integer userId);

    List<Task> findByNoteUserUserIdAndIsCompletedFalseOrderByDueDateAsc(Integer userId);

    List<Task> findByNoteUserUserIdAndDueDateBeforeAndIsCompletedFalseOrderByDueDateAsc(
            Integer userId, LocalDateTime date);

    @Query("SELECT t FROM Task t WHERE t.taskId = :taskId AND t.note.user.userId = :userId")
    Optional<Task> findByTaskIdAndNoteUserUserId(@Param("taskId") Integer taskId, @Param("userId") Integer userId);
}
