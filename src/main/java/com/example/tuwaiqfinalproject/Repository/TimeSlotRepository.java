package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.TimeSlot;
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
    @Query("SELECT t FROM TimeSlot t WHERE t.field.id = ?1 AND t.date = ?2 AND t.start_time >= ?3 AND t.end_time <= ?4")
    List<TimeSlot> findValidSlotsByFieldAndDate(Integer fieldId, LocalDate date, LocalTime openTime, LocalTime closeTime);

}

