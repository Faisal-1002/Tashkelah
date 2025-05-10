package com.example.tuwaiqfinalproject.Controller;
import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.DTO.FieldDTO;
import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Repository.OrganizerRepository;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.FieldService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    private final OrganizerRepository organizerRepository;

    @GetMapping("/all")
    public ResponseEntity<?> getAllFields() {
        return ResponseEntity.status(200).body(fieldService.getAllFields());
    }

//    @PostMapping("/add/{sport_id}")
//    public ResponseEntity<?> addField(@AuthenticationPrincipal User user, @PathVariable Integer sport_id, @ModelAttribute FieldDTO fieldDTO, @RequestPart MultipartFile photoFile) {
//        fieldService.addField(user.getId(), sport_id, fieldDTO, photoFile);
//        return ResponseEntity.status(200).body(new ApiResponse("Field added successfully"));
//    }

    @PutMapping("/update/{fieldId}")
    public ResponseEntity<?> updateField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @RequestBody FieldDTO fieldDTO) {
        fieldService.updateField(user.getId(), fieldId, fieldDTO);
        return ResponseEntity.status(200).body(new ApiResponse("Field updated successfully"));
    }

    @DeleteMapping("/delete/{fieldId}")
    public ResponseEntity<?> deleteField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId) {
        fieldService.deleteField(user.getId(), fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("Field deleted successfully"));
    }

    @GetMapping("/getBySportAndCity/{sport}")
    public ResponseEntity<?> getFieldBySportAndCity(@AuthenticationPrincipal User user, @PathVariable String sport) {
        return ResponseEntity.status(200).body(fieldService.getFieldBySportAndCity(user.getId(), sport));
    }

    @PutMapping("/choseField/{fieldId}/{sportName}")
    public ResponseEntity<?> choseField(@AuthenticationPrincipal User user, @PathVariable Integer fieldId, @PathVariable String sportName) {
        fieldService.playerChoseAFieldForAPublicMatch(sportName, user.getId(), fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("The stadium has been successfully selected"));
    }

    @PutMapping("/private-match/assign-field/{fieldId}")
    public ResponseEntity<?> assignFieldToPrivateMatch(@PathVariable Integer fieldId, @AuthenticationPrincipal User user) {
        fieldService.playerChoseAFieldForPrivateMatch(user.getId(), fieldId);
        return ResponseEntity.status(200).body(new ApiResponse("Field assigned successfully"));
    }

    //Taha - get photo
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


    //Taha
    @GetMapping("/organizer-fields/{organizerId}")
    public ResponseEntity getOrganizerFields(@AuthenticationPrincipal User user) {
       List<Field> fields = fieldService.getAllOrganizerFields(user.getId());
        return ResponseEntity.status(200).body(fields);
    }

}

