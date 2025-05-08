package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.PublicMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicMatchRepository extends JpaRepository<PublicMatch, Integer> {
    PublicMatch findPublicMatchById(Integer id);
}
