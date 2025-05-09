package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.TeamB;
import com.example.tuwaiqfinalproject.Service.TeamBService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/teamb")
@RequiredArgsConstructor
public class TeamBController {

    private final TeamBService teamBService;

    @PostMapping("/add")
    public ResponseEntity addTeamB(@AuthenticationPrincipal PublicMatch publicMatch,
                                   @RequestBody TeamB teamB) {
        teamBService.addTeamB(publicMatch.getId(), teamB);
        return ResponseEntity.status(200).body("TeamB added successfully");
    }

    @PutMapping("/update/{teamBId}")
    public ResponseEntity updateTeamB(@AuthenticationPrincipal PublicMatch publicMatch,
                                      @PathVariable Integer teamBId,
                                      @RequestBody TeamB teamB) {
        teamBService.updateTameB(publicMatch, teamBId, teamB);
        return ResponseEntity.status(200).body("TeamB updated successfully");
    }

    @DeleteMapping("/delete/{teamBId}")
    public ResponseEntity deleteTeamB(@AuthenticationPrincipal PublicMatch publicMatch,
                                      @PathVariable Integer teamBId) {
        teamBService.deleteTeamB(publicMatch, teamBId);
        return ResponseEntity.status(200).body("TeamB deleted successfully");
    }
}