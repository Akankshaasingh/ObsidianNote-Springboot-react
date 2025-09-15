package com.techm.service;

import com.techm.model.Reminder;
import com.techm.repository.ReminderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReminderService {

    @Autowired
    private ReminderRepository repository;

    public List<Reminder> getAll() {
        return repository.findAll();
    }

    public Reminder getById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public Reminder save(Reminder obj) {
        return repository.save(obj);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
