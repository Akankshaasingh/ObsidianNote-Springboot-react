package com.techm.controller;

import com.techm.model.Flashcard;
import com.techm.service.FlashcardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {

    @Autowired
    private FlashcardService service;

    @GetMapping
    public List<Flashcard> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Flashcard getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public Flashcard create(@RequestBody Flashcard obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public Flashcard update(@PathVariable Integer id, @RequestBody Flashcard obj) {
        obj.setFlashcardId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
