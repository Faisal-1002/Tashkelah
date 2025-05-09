package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.DTO.FieldDTO;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/field")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllFields() {
        return ResponseEntity.status(200).body(fieldService.getAllFields());
    }

    @PostMapping("/add")
    public ResponseEntity<?> addField(@AuthenticationPrincipal Organizer organizer, @ModelAttribute FieldDTO fieldDTO, @RequestPart MultipartFile photoFile) {
        fieldService.addField(organizer.getId(), fieldDTO, photoFile);
        return ResponseEntity.status(200).body("Field added successfully");
    }

    @PutMapping("/update/{fieldId}")
    public ResponseEntity<?> updateField(@AuthenticationPrincipal Organizer organizer,@PathVariable Integer fieldId,@RequestBody FieldDTO fieldDTO) {
        fieldService.updateField(organizer, fieldId, fieldDTO);
        return ResponseEntity.status(200).body("Field updated successfully");
    }

    @DeleteMapping("/delete/{fieldId}")
    public ResponseEntity<?> deleteField(@AuthenticationPrincipal Organizer organizer,@PathVariable Integer fieldId) {
        fieldService.deleteField(organizer, fieldId);
        return ResponseEntity.status(200).body("Field deleted successfully");
    }

    @GetMapping("getBySportAndCity/{city}/{sport}")
    public ResponseEntity<?> getFieldBySportAndCity(@PathVariable String city, @PathVariable String sport ){
        return ResponseEntity.status(200).body(fieldService.getFieldBySportAndCity(city,sport));
    }

    @PutMapping("choseField/{fieldId}/{sportName}")
    public ResponseEntity<?> choseField(@AuthenticationPrincipal Player player, @PathVariable Integer fieldId, @PathVariable String sportName){
        fieldService.choseField(sportName,player.getId(),fieldId);
        return ResponseEntity.status(200).body("The stadium has been successfully selected.");
    }

}