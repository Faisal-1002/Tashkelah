package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final PlayerRepository playerRepository;
    private final PublicMatchRepository publicMatchRepository;
    private final FieldRepository fieldRepository;

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public TimeSlot getTimeSlotById(Integer id) {
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotById(id);
        if (timeSlot == null)
            throw new ApiException("TimeSlot not found");
        return timeSlot;
    }

    // Faisal - Add Time slots for a given field and date
    public void createFullDayTimeSlots(Integer fieldId, LocalDate date) {
        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        List<TimeSlot> timeSlots = new ArrayList<>();

        for (int hour = field.getOpen_time().getHour(); hour < field.getClose_time().getHour(); hour++) {
            LocalTime start = LocalTime.of(hour, 0);
            LocalTime end = LocalTime.of(hour + 1, 0);

            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setDate(date);
            slot.setStart_time(start);
            slot.setEnd_time(end);
            slot.setStatus("AVAILABLE");
            slot.setPrice(field.getPrice());

            timeSlots.add(slot);
        }
        timeSlotRepository.saveAll(timeSlots);
    }

    public void addTimeSlotWithPublicMatch(TimeSlot timeSlot,Integer publicMatchId,Integer fieldId) {
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(publicMatchId);
        if (publicMatch == null){
            throw new ApiException("TimeSlot not found");}
        Field field=fieldRepository.findFieldById(fieldId);
        if (field == null){
            throw new ApiException("TimeSlot not found");}
        timeSlot.setField(field);
        timeSlot.setPublic_match(publicMatch);
        timeSlotRepository.save(timeSlot);
    }

    public void updateTimeSlot(Integer id, TimeSlot updatedSlot) {
        TimeSlot existing = timeSlotRepository.findTimeSlotById(id);
        if (existing == null)
            throw new ApiException("TimeSlot not found");

        updatedSlot.setId(existing.getId());
        timeSlotRepository.save(updatedSlot);
    }

    public void deleteTimeSlot(Integer id) {
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotById(id);
        if (timeSlot == null)
            throw new ApiException("TimeSlot not found");
        timeSlotRepository.delete(timeSlot);
    }

    // 25. Faisal - Time slots for the assign filed - Tested
    public List<TimeSlot> getTimeSlotsForPrivateMatchField(Integer userId, LocalDate date) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PrivateMatch match = player.getPrivate_match();
        if (match == null || !match.getStatus().equals("CREATED"))
            throw new ApiException("Private match not found or its status is not CREATED");

        Field field = match.getField();
        if (field == null)
            throw new ApiException("No field assigned to this match");

        return timeSlotRepository.findValidSlotsByFieldAndDate(
                field.getId(),
                date,
                field.getOpen_time(),
                field.getClose_time()
        );
    }


}
