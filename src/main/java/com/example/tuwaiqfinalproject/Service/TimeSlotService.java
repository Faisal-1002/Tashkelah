package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final PlayerRepository playerRepository;
    private final PublicMatchRepository publicMatchRepository;
    private final FieldRepository fieldRepository;
    private final PrivateMatchRepository privateMatchRepository;


    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    // 42. Faisal - Add Time slots by id - Tested
    public TimeSlot getTimeSlotById(Integer id) {
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotById(id);
        if (timeSlot == null)
            throw new ApiException("TimeSlot not found");
        return timeSlot;
    }

    // 43. Faisal - Add Time slots for a given field and date - Tested
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

//    public void addTimeSlotWithPublicMatch(TimeSlot timeSlot,Integer publicMatchId,Integer fieldId) {
//        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(publicMatchId);
//        if (publicMatch == null){
//            throw new ApiException("TimeSlot not found");}
//        Field field=fieldRepository.findFieldById(fieldId);
//        if (field == null){
//            throw new ApiException("TimeSlot not found");}
//        timeSlot.setField(field);
//        timeSlot.setPublic_match(publicMatch);
//        timeSlotRepository.save(timeSlot);
//    }

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

    // 44. Faisal - Time slots for the assign filed - Tested
    public List<TimeSlot> getTimeSlotsForPrivateMatch(Integer userId, Integer privateMatchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PrivateMatch match = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (match == null || !match.getStatus().equals("FIELD_ASSIGNED"))
            throw new ApiException("Private match not found or not in FIELD_ASSIGNED status");

        if (!match.getPlayer().getId().equals(player.getId()))
            throw new ApiException("You are not the owner of this private match");

        Field field = match.getField();
        if (field == null)
            throw new ApiException("No field assigned to this match");

        return timeSlotRepository.findTimeSlotsByFieldAndStatus(field, "AVAILABLE");
    }

    // 58. Faisal - Assign time slots for private match - Tested
    public void assignTimeSlotsToPrivateMatch(Integer userId, Integer matchId, List<Integer> slotIds) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null) throw new ApiException("Player not found");

        PrivateMatch match = privateMatchRepository.findPrivateMatchById(matchId);
        if (match == null || !match.getStatus().equals("FIELD_ASSIGNED"))
            throw new ApiException("Match not found or not in FIELD_ASSIGNED status");

        if (!match.getPlayer().getId().equals(player.getId()))
            throw new ApiException("You are not the owner of this private match");

        Field field = match.getField();
        if (field == null) throw new ApiException("No field assigned");

        List<TimeSlot> slots = timeSlotRepository.findAllById(slotIds);
        if (slots.size() != slotIds.size())
            throw new ApiException("One or more time slots not found");

        for (TimeSlot slot : slots) {
            if (!slot.getField().getId().equals(field.getId()))
                throw new ApiException("One or more slots do not belong to the assigned field");
            if (!slot.getStatus().equalsIgnoreCase("AVAILABLE"))
                throw new ApiException("One or more slots are already booked");
        }

        // Ensure continuity
        slots.sort(Comparator.comparing(TimeSlot::getStart_time));
        for (int i = 1; i < slots.size(); i++) {
            if (!slots.get(i - 1).getEnd_time().equals(slots.get(i).getStart_time())) {
                throw new ApiException("Time slots must be continuous");
            }
        }

        // Set the reverse mapping
        for (TimeSlot slot : slots) {
            slot.setPrivate_match(match);
        }

        // Save the slots and then the match
        timeSlotRepository.saveAll(slots);
        match.setTime_slots(slots);
        match.setStatus("TIME_RESERVED");
        privateMatchRepository.save(match);
    }

}
