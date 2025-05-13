package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.DTO.FieldDTO;
import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/v1/field")
@RequiredArgsConstructor
public class FieldController {

    private final FieldService fieldService;

    //PERMIT ALL
    @GetMapping("/all")
    public ResponseEntity<?> getAllFields() {
        return ResponseEntity.status(200).body(fieldService.getAllFields());
    }

    //ORGANIZER
    @PostMapping("/add/{sportId}")
    public ResponseEntity<?> addField(@AuthenticationPrincipal User user, @PathVariable Integer sportId, @ModelAttribute FieldDTO fieldDTO, @RequestPart MultipartFile photo) {
        fieldService.addField(user.getId(), sportId, fieldDTO, photo);
        return ResponseEntity.status(200).body(new ApiResponse("Field added successfully"));
    }

    //ORGANIZER
    @PutMapping("/update/{fieldId}")
    public ResponseEntity<?> updateMyField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @ModelAttribute FieldDTO fieldDTO, @RequestPart(required = false) MultipartFile photoFile) {
        fieldService.updateField(user.getId(), fieldId, fieldDTO, photoFile);
        return ResponseEntity.status(200).body(new ApiResponse("Field updated successfully"));
    }

    //ORGANIZER
    @DeleteMapping("/delete/{fieldId}")
    public ResponseEntity<?> deleteField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        fieldService.deleteField(user.getId(), fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("Field deleted successfully"));
    }

    //PLAYER
    @GetMapping("/getBySportAndCity/{sportId}")
    public ResponseEntity<?> getFieldBySportAndAddress(@AuthenticationPrincipal User user, @PathVariable Integer sportId){
       return ResponseEntity.status(200).body( fieldService.getFieldBySportAndAddress(user.getId(),sportId));
    }

    //PLAYER
    @GetMapping("/getByDetailsSportAndCity/{sportId}")
    public ResponseEntity<?> getDetailsFieldBySportAndAddress(@AuthenticationPrincipal User user, @PathVariable Integer sportId) {
        return ResponseEntity.status(200).body(fieldService.getDetailsFieldBySportAndAddress(user.getId(), sportId));
    }

    //PERMIT ALL
    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<byte[]> getImage(@PathVariable String filename) throws IOException {
        Path imagePath = Paths.get("uploads", filename);

        if (!Files.exists(imagePath)) {
            throw new ApiException("Image not found");
        }

        byte[] imageBytes = Files.readAllBytes(imagePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(getMediaType(filename));
        return new ResponseEntity<>(imageBytes, headers, 200);
    }

    private MediaType getMediaType(String filename) {
        if (filename.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return MediaType.IMAGE_JPEG;
        return MediaType.APPLICATION_OCTET_STREAM;
    }

    //ORGANIZER
    @GetMapping("/organizer-fields")
    public ResponseEntity<?> getOrganizerFields(@AuthenticationPrincipal User user) {
       List<Field> fields = fieldService.getAllOrganizerFields(user.getId());
        return ResponseEntity.status(200).body(fields);
    }

    //ORGANIZER
    @GetMapping("/booked-slots/{fieldId}")
    public ResponseEntity<?> getBookedTimeSlots(@AuthenticationPrincipal User user,@PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(fieldService.getBookedTimeSlotsForField(user.getId(), fieldId));
    }

    //ORGANIZER
    @GetMapping("/available-slots/{fieldId}")
    public ResponseEntity<?> getAvailableTimeSlots(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        return ResponseEntity.status(200).body(fieldService.getAvailableTimeSlots(user.getId(), fieldId));
    }

    //PLAYER
    @PostMapping("/private-match/{privateMatchId}/assign-field/{fieldId}")
    public ResponseEntity<?> assignFieldToPrivateMatch(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId, @PathVariable Integer fieldId) {
        fieldService.playerChoseAFieldForPrivateMatch(user.getId(), privateMatchId, fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("Field assigned to private match successfully."));
    }

    //ADMIN
    @GetMapping("/field/{id}")
    public ResponseEntity<?> getFieldById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(fieldService.getFieldById(id));
    }

}

