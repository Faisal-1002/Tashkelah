package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Service.PublicMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
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

    @PostMapping("/addPublicMatch/{fieldId}/{timeSlotId}")
    public ResponseEntity<?> addPublicMatch(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @RequestParam List<Integer> timeSlotId, @RequestBody @Valid PublicMatch match) {
        publicMatchService.addPublicMatch(user.getId(),match,fieldId,timeSlotId);
        return ResponseEntity.status(200).body(new ApiResponse("Public match added successfully"));
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
    public ResponseEntity<?> getMatches(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(publicMatchService.showFieldMatches(fieldId, user.getId()));
    }

    @PutMapping("/PlayWithPublicTeam/{sportId}/{fieldId}")
    public ResponseEntity<?> PlayWithPublicTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        publicMatchService.PlayWithPublicMatch(sportId, fieldId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("You have been entered into the public match."));
    }

    @GetMapping("/getMatchAndTeam/{sportId}/{fieldId}")
    public ResponseEntity<?> getMatchAndTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(publicMatchService.getAllAvailablePublicMatches(user.getId(), sportId, fieldId));

    }

    @GetMapping("/getTeams/{publicMatchId}")
    public ResponseEntity<?> getTeamsForPublicMatch(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId){
        return ResponseEntity.status(200).body(publicMatchService.getTeamsForPublicMatch(user.getId(),publicMatchId));
    }

    @PutMapping("/selectTeam/{sportId}/{fieldId}/{matchId}/{teamId}")
    public ResponseEntity<?> selectTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId, @PathVariable Integer matchId, @PathVariable Integer teamId) {
        publicMatchService.PublicTeamSelection(user.getId(), sportId, fieldId, matchId, teamId);
        return ResponseEntity.status(200).body(new ApiResponse("Your team has been successfully booked."));
    }

    @GetMapping("/chekout/{publicMatchId}/{teamId}")
    public ResponseEntity getPlayerMatchSelection(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId,@PathVariable Integer teamId){
        return ResponseEntity.status(200).body(publicMatchService.getPlayerMatchSelection(user.getId(),publicMatchId,teamId));
    }

    @GetMapping("/not/{bookingId}")
    public ResponseEntity Notifications(@AuthenticationPrincipal User user,@PathVariable Integer bookingId){
        return ResponseEntity.status(200).body(publicMatchService.Notifications(user.getId(),bookingId));
    }

    @PutMapping("/changeStatus/{publicMatchId}")
    public ResponseEntity changeStatusAfterCompleted(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId){
        publicMatchService.changeStatusAfterCompleted(user.getId(),publicMatchId);
        return ResponseEntity.status(200).body(new ApiResponse("The number has been completed."));
    }

}
