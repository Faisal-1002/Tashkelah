package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.DTO.PlayerDTO;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.PlayerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/player")
@RequiredArgsConstructor
public class PlayerController {

    private final PlayerService playerService;

    //ADMIN
    @GetMapping("/all")
    public ResponseEntity<?> getAllPlayers() {
        return ResponseEntity.status(200).body(playerService.getAllPlayers());
    }

    //PLAYER
    @GetMapping("/info")
    public ResponseEntity<?> getMyPlayerInfo(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(playerService.getPlayer(user.getId()));
    }

    //ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlayerById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(playerService.getPlayerById(id));
    }

    //PERMIT ALL
    @PostMapping("/register")
    public ResponseEntity<?> registerPlayer(@RequestBody @Valid PlayerDTO dto) {
        playerService.registerPlayer(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Player registered successfully"));
    }

    //PLAYER
    @PutMapping("/update")
    public ResponseEntity<?> updatePlayer(@AuthenticationPrincipal User user, @RequestBody @Valid PlayerDTO dto) {
        playerService.updatePlayer(user.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Player updated successfully"));
    }

    //ADMIN
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePlayer(@PathVariable Integer id) {
        playerService.deletePlayer(id);
        return ResponseEntity.status(200).body(new ApiResponse("Player deleted successfully"));
    }
}
