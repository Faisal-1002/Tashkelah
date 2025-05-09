package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FieldRepository extends JpaRepository<Field, Integer> {
    Field findFieldById(Integer id);
    List<Field> findFieldByLocation(String location);
//    @Query()
    List<Field>findAllBySportNameAndLocation(String sportName,String city);

}
