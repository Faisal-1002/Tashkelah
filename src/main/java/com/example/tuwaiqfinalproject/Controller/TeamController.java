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

    @GetMapping("/getAllTeam")
    public ResponseEntity<?> getAllTeam(){
        return ResponseEntity.status(200).body(teamService.getAllTeam());
    }

    @PostMapping("/add/{publicId}")
    public ResponseEntity addTeam(@PathVariable Integer publicId, @RequestBody Team team) {
        teamService.addTeam(publicId, team);
        return ResponseEntity.status(200).body("TeamA added successfully");
    }

    @PutMapping("/update/{teamId}")
    public ResponseEntity updateTeam(@AuthenticationPrincipal PublicMatch publicMatch, @PathVariable Integer teamId, @RequestBody Team team) {
        teamService.updateTame(publicMatch, teamId, team);
        return ResponseEntity.status(200).body("TeamA updated successfully");
    }

    @DeleteMapping("/delete/{teamId}")
    public ResponseEntity deleteTeam(@AuthenticationPrincipal PublicMatch publicMatch, @PathVariable Integer teamId) {
        teamService.deleteTeam(publicMatch, teamId);
        return ResponseEntity.status(200).body("TeamA deleted successfully");
    }
}
