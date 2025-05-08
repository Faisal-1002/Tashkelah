package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.patterns.ConcreteCflowPointcut;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final SportRepository sportRepository;
    private final PlayerRepository playerRepository;
    private final AuthRepository authRepository;
    private final FieldRepository fieldRepository;
    private final PublicMatchRepository publicMatchRepository;

    public List<TimeSlot> getAllTimeSlots() {
        return timeSlotRepository.findAll();
    }

    public TimeSlot getTimeSlotById(Integer id) {
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotById(id);
        if (timeSlot == null)
            throw new ApiException("TimeSlot not found");
        return timeSlot;
    }

    public void addTimeSlot(TimeSlot timeSlot) {
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

}
