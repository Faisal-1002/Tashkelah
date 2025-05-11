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

    //Taha
    @GetMapping("/field/{fieldId}/matches")
    public ResponseEntity<List<PublicMatch>> getMatches(@AuthenticationPrincipal User user, @PathVariable Integer fieldId){
        return ResponseEntity.ok(publicMatchService.showFieldMatches(fieldId, user.getId()));
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

    @PutMapping("/selectTeam/{sportId}/{fieldId}/{matchId}/{teamId}")
    public ResponseEntity<?> selectTeam(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @PathVariable Integer fieldId, @PathVariable Integer matchId, @PathVariable Integer teamId) {
        publicMatchService.PublicTeamSelection(user.getId(), sportId, fieldId, matchId, teamId);
        return ResponseEntity.status(200).body(new ApiResponse("Your team has been successfully booked."));
    }
    @GetMapping("/chekout/{publicMatchId}/{teamId}")
    public ResponseEntity getPlayerMatchSelection(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId,@PathVariable Integer teamId){
        return ResponseEntity.status(200).body(publicMatchService.getPlayerMatchSelection(user.getId(),publicMatchId,teamId));
    }
    @GetMapping("not/{bookingId}")
    public ResponseEntity no(@AuthenticationPrincipal User user,@PathVariable Integer bookingId){
        publicMatchService.Notifications(user.getId(),bookingId);
        return ResponseEntity.status(200).body(new ApiResponse("تم تسجيلك في المباراة!\n" +
                "بيوصلك التأكيد على البريد الإلكتروني\n" +
                "خلك جاهز. "));
    }
    @PutMapping("changeStatus/{publicMatchId}")
    public ResponseEntity changeStatusAfterCompleted(@AuthenticationPrincipal User user,@PathVariable Integer publicMatchId){
        publicMatchService.changeStatusAfterCompleted(user.getId(),publicMatchId);
        return ResponseEntity.status(200).body(new ApiResponse("The number has been completed."));
    }
}
