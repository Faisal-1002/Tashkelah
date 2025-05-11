package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.User;
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

    @GetMapping("getById/{id}")
    public ResponseEntity<?> getPublicMatchById(@PathVariable Integer id) {
        PublicMatch match = publicMatchService.getPublicMatchById(id);
        return ResponseEntity.status(200).body(match);
    }

    @PostMapping("/addPublicMatch/{fieldId}")
    public ResponseEntity<?> addPublicMatch(@AuthenticationPrincipal User user,@PathVariable Integer fieldId, @RequestBody @Valid PublicMatch match) {
        publicMatchService.addPublicMatch(user.getId(),match,fieldId);
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

    @PutMapping("/PlayWithPublicTeam/{sportId}/{fieldId}")
    public ResponseEntity<?> PlayWithPublicTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        publicMatchService.PlayWithPublicMatch(sportId, fieldId, user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("You have entered the general team."));
    }

    @GetMapping("/getMatchAndTeam/{sportId}/{fieldId}")
    public ResponseEntity<?> getMatchAndTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(publicMatchService.getAllAvailablePublicMatches(user.getId(), sportId, fieldId));
    }

    @GetMapping("/getTeams/{publicMatchId}")
    public ResponseEntity<?> getTeamsForPublicMatch(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId){
        return ResponseEntity.status(200).body(publicMatchService.getTeamsForPublicMatch(user.getId(),publicMatchId));
    }

    @PutMapping("/selectTeam/{sportId}/{fieldId}/{teamName}")
    public ResponseEntity<?> selectTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId, @PathVariable String teamName) {
        publicMatchService.PublicTeamSelection(user.getId(), sportId, fieldId, teamName);
        return ResponseEntity.status(200).body(new ApiResponse("Your team has been successfully booked."));
    }
  
    @GetMapping("/chekout/{publicMatchId}")
    public ResponseEntity<?> getPlayerMatchSelection(@AuthenticationPrincipal Player player,@PathVariable Integer publicMatchId){
        return ResponseEntity.status(200).body(publicMatchService.getPlayerMatchSelection(player.getId(),publicMatchId));

    }
}
