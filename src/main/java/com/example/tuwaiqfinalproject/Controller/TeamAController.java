package com.example.tuwaiqfinalproject.Controller;


import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.Team;
import com.example.tuwaiqfinalproject.Service.TeamAService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teama")
@RequiredArgsConstructor
public class TeamAController {

    private final TeamAService teamAService;

    @PostMapping("/add")
    public ResponseEntity addTeamA(@AuthenticationPrincipal PublicMatch publicMatch,
                                   @RequestBody Team team) {
        teamAService.addTeamA(publicMatch.getId(), team);
        return ResponseEntity.status(200).body("TeamA added successfully");
    }

    @PutMapping("/update/{teamAId}")
    public ResponseEntity updateTeamA(@AuthenticationPrincipal PublicMatch publicMatch,
                                      @PathVariable Integer teamAId,
                                      @RequestBody Team team) {
        teamAService.updateTeamA(publicMatch, teamAId, team);
        return ResponseEntity.status(200).body("TeamA updated successfully");
    }

    @DeleteMapping("/delete/{teamAId}")
    public ResponseEntity deleteTeamA(@AuthenticationPrincipal PublicMatch publicMatch,
                                      @PathVariable Integer teamAId) {
        teamAService.deleteTeamA(publicMatch, teamAId);
        return ResponseEntity.status(200).body("TeamA deleted successfully");
    }
}
