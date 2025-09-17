package com.techm.controller;

import com.techm.model.Task;
import com.techm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@CrossOrigin(origins = "*", maxAge = 3600)
public class TaskController {

    @Autowired
    private TaskService taskService;

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllUserTasks(@RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Task> tasks = taskService.getAllUserTasks(token);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/note/{noteId}")
    public ResponseEntity<List<Task>> getTasksByNote(@PathVariable Integer noteId,
                                                     @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            List<Task> tasks = taskService.getAllTasksByNote(noteId, token);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/note/{noteId}")
    public ResponseEntity<Task> createTask(@PathVariable Integer noteId,
                                           @RequestBody Task task,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Task savedTask = taskService.createTask(noteId, task, token);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Integer id,
                                           @RequestBody Task task,
                                           @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Task updatedTask = taskService.updateTask(id, task, token);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{id}/toggle")
    public ResponseEntity<Task> toggleTaskComplete(@PathVariable Integer id,
                                                   @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            Task task = taskService.toggleTaskComplete(id, token);
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Integer id,
                                        @RequestHeader("Authorization") String authHeader) {
        try {
            String token = extractToken(authHeader);
            taskService.deleteTask(id, token);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Legacy endpoints for compatibility
    @GetMapping("/legacy")
    public List<Task> getAll() {
        return taskService.getAll();
    }

    @GetMapping("/legacy/{id}")
    public Task getById(@PathVariable Integer id) {
        return taskService.getById(id);
    }

    @PostMapping("/legacy")
    public Task create(@RequestBody Task obj) {
        return taskService.save(obj);
    }
}