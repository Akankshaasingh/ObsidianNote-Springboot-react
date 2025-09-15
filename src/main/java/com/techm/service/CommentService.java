package com.techm.service;

import com.techm.model.Comment;
import com.techm.repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository repository;

    public List<Comment> getAll() {
        return repository.findAll();
    }

    public Comment getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Comment save(Comment obj) {
        return repository.save(obj);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
