package com.techm.service;

import com.techm.model.Flashcard;
import com.techm.repository.FlashcardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlashcardService {

    @Autowired
    private FlashcardRepository repository;

    public List<Flashcard> getAll() {
        return repository.findAll();
    }

    public Flashcard getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public Flashcard save(Flashcard obj) {
        return repository.save(obj);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
