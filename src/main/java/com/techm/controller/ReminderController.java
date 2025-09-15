package com.techm.controller;

import com.techm.model.Reminder;
import com.techm.service.ReminderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reminders")
public class ReminderController {

    @Autowired
    private ReminderService service;

    @GetMapping
    public List<Reminder> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Reminder getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Reminder create(@RequestBody Reminder obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public Reminder update(@PathVariable Integer id, @RequestBody Reminder obj) {
        obj.setReminderId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }
}
