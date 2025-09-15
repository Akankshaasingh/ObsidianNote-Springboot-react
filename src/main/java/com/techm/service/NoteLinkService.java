package com.techm.service;

import com.techm.model.NoteLink;
import com.techm.repository.NoteLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteLinkService {

    @Autowired
    private NoteLinkRepository repository;

    public List<NoteLink> getAll() {
        return repository.findAll();
    }

    public NoteLink getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public NoteLink save(NoteLink obj) {
        return repository.save(obj);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
