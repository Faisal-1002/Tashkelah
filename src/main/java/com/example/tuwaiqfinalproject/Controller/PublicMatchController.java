package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
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

    @GetMapping("/{id}")
    public ResponseEntity<?> getPublicMatchById(@PathVariable Integer id) {
        PublicMatch match = publicMatchService.getPublicMatchById(id);
        return ResponseEntity.status(200).body(match);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPublicMatch(@RequestBody @Valid PublicMatch match) {
        publicMatchService.addPublicMatch(match);
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

    @PutMapping("PlayWithPublicTeam/{sportId}/{fieldId}")
    public ResponseEntity PlayWithPublicTeam(@AuthenticationPrincipal Player player, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        publicMatchService.PlayWithPublicTeam(sportId, fieldId, player.getId());
        return ResponseEntity.status(200).body(new ApiResponse("You have entered the general team."));
    }

    @GetMapping("getMatchAndTeam/{sportId}/{fieldId}/{publicMatchId}")
    public ResponseEntity getMatchAndTeam(@AuthenticationPrincipal Player player, @PathVariable Integer sportId, @PathVariable Integer fieldId, @PathVariable Integer publicMatchId) {
        return ResponseEntity.status(200).body(publicMatchService.getMatchAndTeam(player.getId(), sportId, fieldId, publicMatchId));
    }

    @PutMapping("selectTeam/{sportId}/{fieldId}")
    public ResponseEntity selectTeam(@AuthenticationPrincipal Player player, @PathVariable Integer sportId, @PathVariable Integer fieldId) {
        publicMatchService.selectTeam(player.getId(), sportId, fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("Your team has been successfully booked."));
    }
}
