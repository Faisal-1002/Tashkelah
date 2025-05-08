package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.TimeSlot;
import com.example.tuwaiqfinalproject.Repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;

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
