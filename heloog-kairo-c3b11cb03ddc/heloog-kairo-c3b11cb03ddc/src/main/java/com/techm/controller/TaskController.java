package com.techm.controller;

import com.techm.model.Task;
import com.techm.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService service;

    @GetMapping
    public List<Task> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Task getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public Task create(@RequestBody Task obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public Task update(@PathVariable Integer id, @RequestBody Task obj) {
        obj.setTaskId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
