package com.techm.service;

import com.techm.model.SearchLog;
import com.techm.repository.SearchLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchLogService {

    @Autowired
    private SearchLogRepository repository;

    public List<SearchLog> getAll() {
        return repository.findAll();
    }

    public SearchLog getById(Integer id) {
        return repository.findById(id).orElse(null);
    }

    public SearchLog save(SearchLog obj) {
        return repository.save(obj);
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }
}
