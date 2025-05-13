package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.DTO.OrganizerDTO;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.OrganizerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/organizer")
@RequiredArgsConstructor
public class OrganizerController {

    private final OrganizerService organizerService;

    //ADMIN
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrganizers() {
        return ResponseEntity.status(200).body(organizerService.getAllOrganizers());
    }

    //ORGANIZER
    @GetMapping("/info")
    public ResponseEntity<?> getMyOrganizerInfo(@AuthenticationPrincipal User user) {
        Organizer organizer = organizerService.getOrganizer(user.getId());
        return ResponseEntity.status(200).body(organizer);
    }

    //ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrganizerById(@PathVariable Integer id) {
        Organizer organizer = organizerService.getOrganizerById(id);
        return ResponseEntity.status(200).body(organizer);
    }

    //PERMIT ALL
    @PostMapping("/register")
    public ResponseEntity<?> registerOrganizer(@RequestBody @Valid OrganizerDTO dto) {
        organizerService.registerOrganizer(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Organizer registered successfully"));
    }

    //ORGANIZER
    @PutMapping("/update")
    public ResponseEntity<?> updateOrganizer(@AuthenticationPrincipal User user, @RequestBody @Valid OrganizerDTO dto) {
        organizerService.updateOrganizer(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Organizer updated successfully"));
    }

    //ADMIN
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteOrganizer(@AuthenticationPrincipal User user) {
        organizerService.deleteOrganizer(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Organizer deleted successfully"));
    }

    //ADMIN
    @PutMapping("/approve/{organizerId}")
    public ResponseEntity<?> approveOrganizer(@PathVariable Integer organizerId) {
        organizerService.approveOrganizer(organizerId);
        return ResponseEntity.status(200).body(new ApiResponse("Organizer license approval status updated and email sent."));
    }

    //ADMIN
    @PutMapping("/reject/{organizerId}")
    public ResponseEntity<?> rejectOrganizer(@PathVariable Integer organizerId, @PathVariable Boolean isARejected) {
        organizerService.rejectOrganizer(organizerId);
        return ResponseEntity.status(200).body(new ApiResponse("Organizer license approval status updated and email sent."));
    }

    //ADMIN
    @PutMapping("/block/{organizerId}")
    public ResponseEntity<?> blockOrganizer(@PathVariable Integer organizerId) {
        organizerService.blockOrganizer(organizerId);
        return ResponseEntity.status(200).body(new ApiResponse("Organizer license approval status updated and email sent."));
    }

}
