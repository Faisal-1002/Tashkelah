package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;

import com.example.tuwaiqfinalproject.Model.Organizer;
import org.springframework.beans.factory.annotation.Qualifier;

import com.example.tuwaiqfinalproject.Model.Sport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {

    @Query("SELECT f FROM Field f JOIN FETCH f.organizer WHERE f.id = :id")
    Field findFieldById(Integer id);
    List<Field> findFieldByOrganizer(Organizer organizer);
    @Query("SELECT f FROM Field f WHERE f.sport.id = ?1 AND f.address = ?2")
    List<Field> findAllBySportIdAndLocation(Integer sportId, String location);


}
