package com.techm.service;

import com.techm.model.Task;
import com.techm.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository repository;

    public List<Task> getAll() {
        return repository.findAll();
    }

    public Task getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Task save(Task obj) {
        return repository.save(obj);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
