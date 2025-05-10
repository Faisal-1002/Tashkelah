package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SportRepository extends JpaRepository<Sport, Integer> {
    Sport findSportById(Integer id);
}
