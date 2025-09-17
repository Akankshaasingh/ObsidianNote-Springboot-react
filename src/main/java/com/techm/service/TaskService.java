package com.techm.service;

import com.techm.model.Task;
import com.techm.model.Note;
import com.techm.model.User;
import com.techm.repository.TaskRepository;
import com.techm.repository.NoteRepository;
import com.techm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

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

    public List<Task> getAllTasksByNote(Integer noteId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify note belongs to user
        Note note = noteRepository.findByNoteIdAndUserUserId(noteId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        return taskRepository.findByNoteNoteIdOrderByDueDateAsc(noteId);
    }

    public List<Task> getAllUserTasks(String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        return taskRepository.findByNoteUserUserIdOrderByDueDateAsc(currentUser.getUserId());
    }

    public Task createTask(Integer noteId, Task task, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Verify note belongs to user
        Note note = noteRepository.findByNoteIdAndUserUserId(noteId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Note not found or access denied"));

        task.setNote(note);
        if (task.getIsCompleted() == null) {
            task.setIsCompleted(false);
        }

        return taskRepository.save(task);
    }

    public Task updateTask(Integer taskId, Task taskData, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Task task = taskRepository.findByTaskIdAndNoteUserUserId(taskId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        task.setDescription(taskData.getDescription());
        task.setDueDate(taskData.getDueDate());
        task.setPriority(taskData.getPriority());
        task.setIsCompleted(taskData.getIsCompleted());

        return taskRepository.save(task);
    }

    public Task toggleTaskComplete(Integer taskId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Task task = taskRepository.findByTaskIdAndNoteUserUserId(taskId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        task.setIsCompleted(!task.getIsCompleted());
        return taskRepository.save(task);
    }

    public void deleteTask(Integer taskId, String token) {
        User currentUser = getCurrentUser(token);
        if (currentUser == null) {
            throw new RuntimeException("User not authenticated");
        }

        Task task = (Task) taskRepository.findByTaskIdAndNoteUserUserId(taskId, currentUser.getUserId())
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));

        taskRepository.delete(task);
    }

    // Legacy methods for compatibility
    public List<Task> getAll() {
        return taskRepository.findAll();
    }

    public Task getById(Integer id) {
        return taskRepository.findById(id).orElse(null);
    }

    public Task save(Task obj) {
        return taskRepository.save(obj);
    }

    public void delete(Integer id) {
        taskRepository.deleteById(id);
    }
}
