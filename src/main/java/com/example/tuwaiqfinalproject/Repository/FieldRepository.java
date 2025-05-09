package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    Field findFieldById(Integer id);
    List<Field> findFieldByLocation(String location);
    @Query("SELECT f FROM Field f WHERE f.sport.name = ?1 AND f.location = ?2")
    List<Field> findAllBySportNameAndCity(String sportName, String location);
}
