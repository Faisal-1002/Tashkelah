package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.PrivateMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/private-match")
@RequiredArgsConstructor
public class PrivateMatchController {

    private final PrivateMatchService privateMatchService;

    //ADMIN
    @GetMapping("/all")
    public ResponseEntity<?> getAllPrivateMatches() {
        return ResponseEntity.status(200).body(privateMatchService.getAllPrivateMatches());
    }

    //ADMIN
    @GetMapping("/{id}")
    public ResponseEntity<?> getPrivateMatchById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(privateMatchService.getPrivateMatchById(id));
    }

    //PLAYER
    @GetMapping("/my-private-matches")
    public ResponseEntity<?> getMyPrivateMatches(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(privateMatchService.getMyPrivateMatches(user.getId()));
    }

    //PLAYER
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer id, @RequestBody @Valid PrivateMatch updatedMatch) {
        privateMatchService.updatePrivateMatch(user.getId(), id, updatedMatch);
        return ResponseEntity.status(200).body(new ApiResponse("Private match updated successfully"));
    }

    //ADMIN
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrivateMatch(@PathVariable Integer id) {
        privateMatchService.deletePrivateMatch(id);
        return ResponseEntity.status(200).body(new ApiResponse("Private match deleted successfully"));
    }

    //PLAYER
    @PostMapping("/create")
    public ResponseEntity<?> createPrivateMatch(@AuthenticationPrincipal User user) {
        privateMatchService.createPrivateMatch(user.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Private match created successfully"));
    }

}
