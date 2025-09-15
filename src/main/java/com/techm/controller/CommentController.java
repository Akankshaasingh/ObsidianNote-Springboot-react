package com.techm.controller;

import com.techm.model.Comment;
import com.techm.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService service;

    @GetMapping
    public List<Comment> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Comment getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public Comment create(@RequestBody Comment obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public Comment update(@PathVariable Integer id, @RequestBody Comment obj) {
        obj.setCommentId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
