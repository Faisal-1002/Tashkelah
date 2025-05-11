package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;

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

    @Query("SELECT f FROM Field f WHERE f.organizer.id = :organizerId")
    List<Field> findFieldByOrganizer_Id(Integer organizerId);
    List<Field> findFieldByLocation(String location);
    List<Field>findAllBySportNameAndLocation(String sportName,String city);
    @Query("SELECT f FROM Field f WHERE f.sport.name = ?1 AND f.location = ?2")
    List<Field> findAllBySportNameAndCity(String sportName, String location);
 
}
