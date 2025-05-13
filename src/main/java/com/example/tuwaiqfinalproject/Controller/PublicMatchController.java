package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Service.PublicMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/v1/public-match")
@RequiredArgsConstructor
public class PublicMatchController {

    private final PublicMatchService publicMatchService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPublicMatches() {
        List<PublicMatch> matches = publicMatchService.getAllPublicMatches();
        return ResponseEntity.status(200).body(matches);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getPublicMatchById(@PathVariable Integer id) {
        PublicMatch match = publicMatchService.getPublicMatchById(id);
        return ResponseEntity.status(200).body(match);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePublicMatch(@PathVariable Integer id, @RequestBody @Valid PublicMatch updatedMatch) {
        publicMatchService.updatePublicMatch(id, updatedMatch);
        return ResponseEntity.status(200).body(new ApiResponse("Public match updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePublicMatch(@PathVariable Integer id) {
        publicMatchService.deletePublicMatch(id);
        return ResponseEntity.status(200).body(new ApiResponse("Public match deleted successfully"));
    }

    @GetMapping("/field/{fieldId}/matches")
    public ResponseEntity<?> getMatchesForOneField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(publicMatchService.getMatchesForOneField(fieldId, user.getId()));
    }

    @PutMapping("/PlayWithPublicTeam/{publicId}/{teamId}")
    public ResponseEntity<?> PlayWithPublicTeam(@AuthenticationPrincipal User user, @PathVariable Integer publicId, @PathVariable Integer teamId) {
        publicMatchService.PlayWithPublicMatch(publicId, teamId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("You have been entered into the public match."));
    }

    @GetMapping("/getMatchByTime/{publicMatchId}")
    public ResponseEntity<?> getMatchAndTeam(@AuthenticationPrincipal User user, @PathVariable Integer publicMatchId) {
        return ResponseEntity.status(200).body(publicMatchService.getAllAvailablePublicMatches(user.getId(), publicMatchId));

    }

    @GetMapping("/getTeams/{publicMatchId}")
    public ResponseEntity<?> getTeamsForPublicMatch(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId){
        return ResponseEntity.status(200).body(publicMatchService.getTeamsForPublicMatch(user.getId(),publicMatchId));
    }

    @GetMapping("/chekout/{publicMatchId}/{teamId}")
    public ResponseEntity getPlayerMatchSelection(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId,@PathVariable Integer teamId){
        return ResponseEntity.status(200).body(publicMatchService.getPlayerMatchSelection(user.getId(),publicMatchId,teamId));
    }

    @GetMapping("/not/{bookingId}")
    public ResponseEntity Notifications(@AuthenticationPrincipal User user,@PathVariable Integer bookingId){
        publicMatchService.Notifications(user.getId(),bookingId);
        return ResponseEntity.status(200).body(new ApiResponse("Booking successful, waiting for more players"));
    }

    @PutMapping("/changeStatus/{publicMatchId}")
    public ResponseEntity changeStatusAfterCompleted(@PathVariable Integer publicMatchId){
        publicMatchService.changeStatusAfterCompleted(publicMatchId);
        return ResponseEntity.status(200).body(new ApiResponse("The number has been completed."));
    }

    // Create a public match by providing only the fieldId as path variable
    @PostMapping("/matches/{fieldId}/slots/{slotIds}")
    public ResponseEntity<?> createMatchFromSlots(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @PathVariable List<Integer> slotIds) {
        publicMatchService.createMatchFromTimeSlots(user.getId(), fieldId, slotIds);
        return ResponseEntity.status(200).body(new ApiResponse("Match created successfully"));
    }

}


