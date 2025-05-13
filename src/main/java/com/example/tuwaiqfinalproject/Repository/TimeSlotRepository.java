package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.TimeSlot;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    TimeSlot findTimeSlotById(Integer id);
    List<TimeSlot> findTimeSlotsByFieldAndStatus(Field field, String status);
    List<TimeSlot> findByFieldAndStatus(Field field, String status);
    @Query("SELECT t FROM TimeSlot t WHERE t.field = ?1 AND t.date = ?2")
    List<TimeSlot> findByFieldAndDate(Field field, LocalDate date);
}


