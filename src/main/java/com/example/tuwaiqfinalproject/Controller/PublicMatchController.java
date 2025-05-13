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

    //ADMIN
    @GetMapping("/all")
    public ResponseEntity<?> getAllPublicMatches() {
        return ResponseEntity.status(200).body(publicMatchService.getAllPublicMatches());
    }

    //ADMIN
    @GetMapping("/getById/{id}")
    public ResponseEntity<?> getPublicMatchById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(publicMatchService.getPublicMatchById(id));
    }

    //PLAYER
    @GetMapping("/my-public-matches")
    public ResponseEntity<?> getMyPublicMatchBookings(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(publicMatchService.getMyPublicMatches(user.getId()));
    }

    //ORGANIZER
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePublicMatch(@AuthenticationPrincipal User user, @PathVariable Integer id, @RequestBody @Valid PublicMatch updatedMatch) {
        publicMatchService.updatePublicMatch(user.getId(), id, updatedMatch);
        return ResponseEntity.status(200).body(new ApiResponse("Public match updated successfully"));
    }

    //ORGANIZER
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePublicMatch(@AuthenticationPrincipal User user, @PathVariable Integer id) {
        publicMatchService.deletePublicMatch(user.getId(), id);
        return ResponseEntity.status(200).body(new ApiResponse("Public match deleted successfully"));
    }

    //ORGANIZER
    @GetMapping("/field/{fieldId}/matches")
    public ResponseEntity<?> getMatchesForOneField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(publicMatchService.getMatchesForOneField(fieldId, user.getId()));
    }

    //PLAYER
    @PutMapping("/PlayWithPublicTeam/{publicId}/{teamId}")
    public ResponseEntity<?> playWithPublicTeam(@AuthenticationPrincipal User user, @PathVariable Integer publicId, @PathVariable Integer teamId) {
        publicMatchService.playWithPublicMatch(user.getId(), publicId, teamId);
        return ResponseEntity.status(200).body(new ApiResponse("You have been entered into the public match."));
    }

    //PLAYER
    @GetMapping("/getMatchByTime/{sportId}/{fieldId}")
    public ResponseEntity<?> getMatchAndTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(publicMatchService.getAllAvailablePublicMatches(user.getId(), sportId, fieldId));
    }

    //PLAYER
    @GetMapping("/getTeams/{publicMatchId}")
    public ResponseEntity<?> getTeamsForPublicMatch(@AuthenticationPrincipal User user, @PathVariable Integer publicMatchId){
        return ResponseEntity.status(200).body(publicMatchService.getTeamsForPublicMatch(user.getId(),publicMatchId));
    }

    //PLAYER
    @GetMapping("/checkout/{publicMatchId}/{teamId}")
    public ResponseEntity<?> getPlayerMatchSelection(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId,@PathVariable Integer teamId){
        return ResponseEntity.status(200).body(publicMatchService.getPlayerMatchSelection(user.getId(),publicMatchId,teamId));
    }

    //AUTO
    @GetMapping("/notifications/{bookingId}")
    public ResponseEntity<?> notifications(@AuthenticationPrincipal User user, @PathVariable Integer bookingId){
        publicMatchService.notifications(user.getId(),bookingId);
        return ResponseEntity.status(200).body(new ApiResponse("Booking successful, waiting for more players"));
    }

    //AUTO
    @PutMapping("/changeStatus/{publicMatchId}")
    public ResponseEntity<?> changeStatusAfterCompleted(@AuthenticationPrincipal User user, @PathVariable Integer publicMatchId){
        publicMatchService.changeStatusAfterCompleted(user.getId(), publicMatchId);
        return ResponseEntity.status(200).body(new ApiResponse("The number has been completed."));
    }

    //ORGANIZER
    @PostMapping("/matches/{fieldId}")
    public ResponseEntity<?> createPublicMatch(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @RequestBody List<Integer> slotIds) {
        publicMatchService.createPublicMatch(user.getId(), fieldId, slotIds);
        return ResponseEntity.status(200).body(new ApiResponse("Match created successfully"));
    }

}


