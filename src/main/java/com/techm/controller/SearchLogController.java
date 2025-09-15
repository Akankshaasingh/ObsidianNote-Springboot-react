package com.techm.controller;

import com.techm.model.SearchLog;
import com.techm.service.SearchLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/searchlogs")
public class SearchLogController {

    @Autowired
    private SearchLogService service;

    @GetMapping
    public List<SearchLog> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public SearchLog getById(@PathVariable Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public SearchLog create(@RequestBody SearchLog obj) {
        return service.save(obj);
    }

    @PutMapping("/{id}")
    public SearchLog update(@PathVariable Integer id, @RequestBody SearchLog obj) {
        obj.setLogId(id);
        return service.save(obj);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }
}
