package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.TimeSlot;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/slot")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    //ADMIN
    @GetMapping("/all")
    public ResponseEntity<?> getAllSlots() {
        List<TimeSlot> slots = timeSlotService.getAllTimeSlots();
        return ResponseEntity.status(200).body(slots);
    }

    //ADMIN
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getSlotById(@PathVariable Integer id) {
        TimeSlot slot = timeSlotService.getTimeSlotById(id);
        return ResponseEntity.status(200).body(slot);
    }

    //ORGANIZER
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSlot(@AuthenticationPrincipal User user, @PathVariable Integer id, @RequestBody @Valid TimeSlot updatedSlot) {
        timeSlotService.updateTimeSlot(user.getId(), id, updatedSlot);
        return ResponseEntity.status(200).body(new ApiResponse("TimeSlot updated successfully"));
    }

    //ORGANIZER
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSlot(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        timeSlotService.deleteTimeSlot(user.getId(), id);
        return ResponseEntity.status(200).body(new ApiResponse("TimeSlot deleted successfully"));
    }

    //PLAYER
    @GetMapping("/private-match/slots/{privateMatchId}")
    public ResponseEntity<?> getAvailableTimeSlotsForPrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        return ResponseEntity.status(200).body(timeSlotService.getTimeSlotsForPrivateMatch(user.getId(), privateMatchId));
    }

    //PLAYER
    @PostMapping("/private-match/{matchId}/assign-slots")
    public ResponseEntity<?> assignTimeSlotsToPrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer matchId, @RequestBody List<Integer> slotIds) {
        timeSlotService.assignTimeSlotsToPrivateMatch(user.getId(), matchId,slotIds);
        return ResponseEntity.status(200).body(new ApiResponse("Time slots assigned successfully"));
    }

    //ORGANIZER
    @PostMapping("/field/{fieldId}/timeslots/create")
    public ResponseEntity<ApiResponse> createTodayTimeSlots(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        timeSlotService.createFieldTimeSlotsManually(user.getId(), fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("Today's time slots created successfully."));
    }

}
