package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Organizer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizerRepository extends JpaRepository<Organizer, Integer> {
    Organizer findOrganizerById(Integer id);
}
