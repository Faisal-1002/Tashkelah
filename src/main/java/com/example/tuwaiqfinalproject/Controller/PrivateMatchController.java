package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Service.PrivateMatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/private-match")
@RequiredArgsConstructor
public class PrivateMatchController {

    private final PrivateMatchService privateMatchService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllPrivateMatches() {
        List<PrivateMatch> matches = privateMatchService.getAllPrivateMatches();
        return ResponseEntity.status(200).body(matches);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPrivateMatchById(@PathVariable Integer id) {
        PrivateMatch match = privateMatchService.getPrivateMatchById(id);
        return ResponseEntity.status(200).body(match);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addPrivateMatch(@RequestBody @Valid PrivateMatch match) {
        privateMatchService.addPrivateMatch(match);
        return ResponseEntity.status(200).body(new ApiResponse("Private match added successfully"));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePrivateMatch(@PathVariable Integer id,
                                                @RequestBody @Valid PrivateMatch updatedMatch) {
        privateMatchService.updatePrivateMatch(id, updatedMatch);
        return ResponseEntity.status(200).body(new ApiResponse("Private match updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deletePrivateMatch(@PathVariable Integer id) {
        privateMatchService.deletePrivateMatch(id);
        return ResponseEntity.status(200).body(new ApiResponse("Private match deleted successfully"));
    }
}
