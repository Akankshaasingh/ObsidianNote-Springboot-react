package com.techm.controller;

import com.techm.model.NoteLink;
import com.techm.service.NoteLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notelinks")
public class NoteLinkController {

    @Autowired
    private NoteLinkService service;

    @GetMapping
    public List<NoteLink> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public NoteLink getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public NoteLink create(@RequestBody NoteLink obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public NoteLink update(@PathVariable Integer id, @RequestBody NoteLink obj) {
        obj.setLinkId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
