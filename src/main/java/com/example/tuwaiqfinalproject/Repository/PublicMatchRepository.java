package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PublicMatchRepository extends JpaRepository<PublicMatch, Integer> {
    PublicMatch findPublicMatchById(Integer id);
    List<PublicMatch> findPublicMatchByField(Field field);
    PublicMatch findFirstByFieldAndStatusAndField_Sport(Field field, String status, Sport sport);
    List<PublicMatch> findPublicMatchesByField(Field field);
}
