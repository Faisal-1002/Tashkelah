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
    private final FieldRepository fieldRepository;
    private final PrivateMatchRepository privateMatchRepository;
    private final OrganizerRepository organizerRepository;

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

    // 43. Faisal - Add Time slots for a given field manually - Tested
    public void createFieldTimeSlotsManually(Integer userId, Integer fieldId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null)
            throw new ApiException("Field not found");

        if (!field.getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("Unauthorized access to this field");

        LocalDate today = LocalDate.now();
        List<TimeSlot> existing = timeSlotRepository.findByFieldAndDate(field, today);
        if (!existing.isEmpty())
            throw new ApiException("Time slots already exist for today");

        List<TimeSlot> slots = generateSlots(field, today);
        timeSlotRepository.saveAll(slots);
    }

    // 63. Faisal - Scheduled adding time slots - Tested
    @Scheduled(cron = "0 0 0 * * *") // At midnight every day
    public void createTimeSlotsAutomatically() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        List<Field> fields = fieldRepository.findAll();
        for (Field field : fields) {
            List<TimeSlot> existing = timeSlotRepository.findByFieldAndDate(field, tomorrow);
            if (existing.isEmpty()) {
                List<TimeSlot> slots = generateSlots(field, tomorrow);
                timeSlotRepository.saveAll(slots);
            }
        }
    }

    // Faisal - Helper method - Tested
    private List<TimeSlot> generateSlots(Field field, LocalDate date) {
        List<TimeSlot> slots = new ArrayList<>();

        LocalTime open = field.getOpen_time();
        LocalTime close = field.getClose_time();

        for (LocalTime time = open; time.isBefore(close); time = time.plusHours(1)) {
            TimeSlot slot = new TimeSlot();
            slot.setField(field);
            slot.setDate(date);
            slot.setStart_time(time);
            slot.setEnd_time(time.plusHours(1));
            slot.setStatus("AVAILABLE");
            slot.setPrice(field.getPrice());
            slots.add(slot);
        }
        return slots;
    }

    public void updateTimeSlot(Integer userId, Integer id, TimeSlot updatedSlot) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        TimeSlot existing = timeSlotRepository.findTimeSlotById(id);
        if (existing == null)
            throw new ApiException("TimeSlot not found");

        Field field = existing.getField();
        if (field == null || !field.getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("You are not allowed to update a timeslot that doesn't belong to your field");

        // Only update allowed fields
        existing.setStart_time(updatedSlot.getStart_time());
        existing.setEnd_time(updatedSlot.getEnd_time());
        existing.setPrice(updatedSlot.getPrice());
        existing.setStatus(updatedSlot.getStatus());

        timeSlotRepository.save(existing);
    }

    public void deleteTimeSlot(Integer userID, Integer id) {
        Organizer organizer = organizerRepository.findOrganizerById(userID);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        TimeSlot timeSlot = timeSlotRepository.findTimeSlotById(id);
        if (timeSlot == null)
            throw new ApiException("TimeSlot not found");

        if (!timeSlot.getField().getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("You are not allowed to delete a timeslot from another organizer's field");

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
