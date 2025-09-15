package com.techm.controller;

import com.techm.model.User;
import com.techm.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService service;

    @GetMapping
    public List<User> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public User getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public User create(@RequestBody User obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Integer id, @RequestBody User obj) {
        obj.setUserId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
