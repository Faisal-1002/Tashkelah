package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Model.TimeSlot;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.PrivateMatchService;
import com.example.tuwaiqfinalproject.Service.TimeSlotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/slot")
@RequiredArgsConstructor
public class TimeSlotController {

    private final TimeSlotService timeSlotService;


    @GetMapping("/all")
    public ResponseEntity<?> getAllSlots() {
        List<TimeSlot> slots = timeSlotService.getAllTimeSlots();
        return ResponseEntity.status(200).body(slots);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getSlotById(@PathVariable Integer id) {
        TimeSlot slot = timeSlotService.getTimeSlotById(id);
        return ResponseEntity.status(200).body(slot);
    }

    @PostMapping("/add/{publicMatchId}/{fieldId}")
    public ResponseEntity<?> addSlot(@RequestBody @Valid TimeSlot timeSlot,@PathVariable Integer publicMatchId,@PathVariable Integer fieldId) {
        timeSlotService.addTimeSlotWithPublicMatch(timeSlot,publicMatchId,fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("TimeSlot added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSlot(@PathVariable Integer id, @RequestBody @Valid TimeSlot updatedSlot) {
        timeSlotService.updateTimeSlot(id, updatedSlot);
        return ResponseEntity.status(200).body(new ApiResponse("TimeSlot updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSlot(@PathVariable Integer id) {
        timeSlotService.deleteTimeSlot(id);
        return ResponseEntity.status(200).body(new ApiResponse("TimeSlot deleted successfully"));
    }

    @GetMapping("/private-match/slots/{privateMatchId}")
    public ResponseEntity<?> getAvailableTimeSlotsForPrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        List<TimeSlot> slots = timeSlotService.getTimeSlotsForPrivateMatch(2, privateMatchId);
        return ResponseEntity.status(200).body(slots);
    }

    @PostMapping("/private-match/{matchId}/assign-slots")
    public ResponseEntity<?> assignTimeSlotsToPrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer matchId, @RequestBody List<Integer> slotIds) {
        timeSlotService.assignTimeSlotsToPrivateMatch(2,matchId,slotIds);
        return ResponseEntity.status(200).body(new ApiResponse("Time slots assigned successfully"));
    }




    @PostMapping("/generate/{fieldId}")
    public ResponseEntity<?> generateTimeSlots(@PathVariable Integer fieldId) {
            timeSlotService.generateTimeSlotsForFieldOnDate(fieldId, LocalDate.now());

            return ResponseEntity.status(200).body("Time slots generated successfully for the field.");

    }

}
