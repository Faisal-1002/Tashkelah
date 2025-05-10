package com.example.tuwaiqfinalproject.Controller;


import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.Team;
import com.example.tuwaiqfinalproject.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teama")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/add/{publicId}")
    public ResponseEntity addTeam(@PathVariable Integer publicId,
                                   @RequestBody Team team) {
        teamService.addTeamA(publicId, team);
        return ResponseEntity.status(200).body("TeamA added successfully");
    }

    @PutMapping("/update/{teamAId}")
    public ResponseEntity updateTeam(@AuthenticationPrincipal PublicMatch publicMatch,
                                      @PathVariable Integer teamAId,
                                      @RequestBody Team team) {
        teamService.updateTameA(publicMatch, teamAId, team);
        return ResponseEntity.status(200).body("TeamA updated successfully");
    }

    @DeleteMapping("/delete/{teamAId}")
    public ResponseEntity deleteTeam(@AuthenticationPrincipal PublicMatch publicMatch,
                                      @PathVariable Integer teamAId) {
        teamService.deleteTeamA(publicMatch, teamAId);
        return ResponseEntity.status(200).body("TeamA deleted successfully");
    }
}
