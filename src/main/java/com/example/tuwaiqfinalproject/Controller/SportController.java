package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.Sport;
import com.example.tuwaiqfinalproject.Service.SportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sports")
@RequiredArgsConstructor
public class SportController {

    private final SportService sportService;

    //PERMIT ALL
    @GetMapping("/all")
    public ResponseEntity<?> getAllSports() {
        List<Sport> sports = sportService.getAllSports();
        return ResponseEntity.status(200).body(sports);
    }

    //PERMIT ALL
    @GetMapping("/get-by-id/{id}")
    public ResponseEntity<?> getSportById(@PathVariable Integer id) {
        Sport sport = sportService.getSportById(id);
        return ResponseEntity.status(200).body(sport);
    }

    //ADMIN
    @PostMapping("/add")
    public ResponseEntity<?> addSport(@RequestBody @Valid Sport sport) {
        sportService.addSport(sport);
        return ResponseEntity.status(200).body(new ApiResponse("Sport added successfully"));
    }

    //ADMIN
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateSport(@PathVariable Integer id, @RequestBody @Valid Sport updatedSport) {
        sportService.updateSport(id, updatedSport);
        return ResponseEntity.status(200).body(new ApiResponse("Sport updated successfully"));
    }

    //ADMIN
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteSport(@PathVariable Integer id) {
        sportService.deleteSport(id);
        return ResponseEntity.status(200).body(new ApiResponse("Sport deleted successfully"));
    }

}
