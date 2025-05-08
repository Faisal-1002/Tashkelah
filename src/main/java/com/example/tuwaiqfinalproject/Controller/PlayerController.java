package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.DTO.PlayerDTO;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPlayers() {
        List<Player> players = playerService.getAllPlayers();
        return ResponseEntity.status(200).body(players);
    }

    @GetMapping("/info")
    public ResponseEntity<?> getMyPlayerInfo(@AuthenticationPrincipal User user) {
        Player player = playerService.getPlayer(user.getId());
        return ResponseEntity.status(200).body(player);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPlayerById(@PathVariable Integer id) {
        Player player = playerService.getPlayerById(id);
        return ResponseEntity.status(200).body(player);
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerPlayer(@RequestBody @Valid PlayerDTO dto) {
        playerService.registerPlayer(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Player registered successfully"));
    }

    @PutMapping("/update")
    public ResponseEntity<?> updatePlayer(@AuthenticationPrincipal User user, @RequestBody @Valid PlayerDTO dto) {
        playerService.updatePlayer(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Player updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deletePlayer(@AuthenticationPrincipal User user) {
        playerService.deletePlayer(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Player deleted successfully"));
    }
}
