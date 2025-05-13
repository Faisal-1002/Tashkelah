package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Model.Team;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
public class TeamController {

    private final TeamService teamService;

    //ADMIN
    @GetMapping("/getAllTeam")
    public ResponseEntity<?> getAllTeam(){
        return ResponseEntity.status(200).body(teamService.getAllTeam());
    }

    //ORGANIZER
    @PutMapping("/update/{teamId}")
    public ResponseEntity<?> updateTeam(@AuthenticationPrincipal User user, @PathVariable Integer teamId, @RequestBody Team team, UserDetails authenticatedPrincipal) {
        teamService.updateTeam(user.getId(), teamId, team);
        return ResponseEntity.status(200).body("TeamA updated successfully");
    }

    //ORGANIZER
    @DeleteMapping("/delete/{teamId}")
    public ResponseEntity<?> deleteTeam(@AuthenticationPrincipal User user, @PathVariable Integer teamId) {
        teamService.deleteTeam(user.getId(), teamId);
        return ResponseEntity.status(200).body("TeamA deleted successfully");
    }

    //ORGANIZER
    @PostMapping("/addTeamsForPublicMatch/{matchId}")
    public ResponseEntity<?> addTeamsForPublicMatch(@AuthenticationPrincipal User user, @PathVariable Integer matchId) {
        teamService.addTeamsForPublicMatch(user.getId(), matchId);
        return ResponseEntity.status(200).body("Team A and Team B added successfully");
    }

    //ADMIN
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getTeamById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(teamService.getTeamById(id));
    }
}
