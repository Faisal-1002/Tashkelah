package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    Field findFieldById(Integer id);


    @Query("SELECT f FROM Field f WHERE f.organizer.id = :organizerId")
    List<Field> findFieldByOrganizer_Id(Integer organizerId);
}
