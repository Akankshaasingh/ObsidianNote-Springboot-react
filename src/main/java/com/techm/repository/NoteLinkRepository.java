package com.techm.repository;

import com.techm.model.NoteLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteLinkRepository extends JpaRepository<NoteLink, Integer> {
}
