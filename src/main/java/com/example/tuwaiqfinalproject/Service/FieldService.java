package com.example.tuwaiqfinalproject.Service;
import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.FieldDTO;
import com.example.tuwaiqfinalproject.DTO.NameCityFieldDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final OrganizerRepository organizerRepository;
    private final SportRepository sportRepository;
    private final PlayerRepository playerRepository;
    private final PrivateMatchRepository privateMatchRepository;
    private final TimeSlotRepository timeSlotRepository;

    public List<Field> getAllFields() {
        return fieldRepository.findAll();
    }

    // 40. Faisal - Get field by id - Tested
    public Field getFieldById(Integer id) {
        return fieldRepository.findFieldById(id);
    }

    // 10. Taha - Public method to allow an approved organizer to add a new field with an image - Tested
    public void addField(Integer organizerId, Integer sportId, FieldDTO fieldDTO, MultipartFile photoFile) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        if (!organizer.getStatus().equals("ACTIVE"))
            throw new ApiException("Your account is not yet approved");
        Sport sport = sportRepository.findSportById(sportId);
        if (sport == null)
            throw new ApiException("Sport not found");
        String photoPath = saveImage(photoFile);
        Field field = new Field(
                null,
                fieldDTO.getName(),
                fieldDTO.getAddress(),
                fieldDTO.getDescription(),
                photoPath,
                fieldDTO.getOpen_time(),
                fieldDTO.getClose_time(),
                sport.getDefault_player_count()*2,
                fieldDTO.getPrice(),
                sport,
                organizer,
                null,
                null,
                null
        );
        fieldRepository.save(field);
    }

    // 11. Taha - Private method to save an uploaded image file - Tested
    private String saveImage(MultipartFile file) {
        // Check if the uploaded file is empty
        if (file.isEmpty()) {
            throw new ApiException("No file selected");
        }

        // Check if the file is an image by verifying the content type
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ApiException("Invalid file type. Only images are allowed.");
        }

        try {
            // Define the directory where images will be saved
            String uploadsDir = "uploads";
            Path uploadsPath = Paths.get(uploadsDir);

            // If the directory does not exist, create it
            if (!Files.exists(uploadsPath)) {
                Files.createDirectories(uploadsPath);
            }

            // Generate a unique file name using UUID to prevent naming conflicts
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // Define the full path where the file will be saved
            Path filePath = uploadsPath.resolve(fileName);

            // Save the file bytes to the specified path
            Files.write(filePath, file.getBytes());

            // Return the name of the saved file (to be stored or returned as needed)
            return fileName;

        } catch (IOException e) {
            e.printStackTrace(); // Print the stack trace to the console for debugging
            throw new ApiException("Failed to save image");
        }
    }

    // 12. Taha - Allows an organizer to update an existing field's information and photo - Tested
    public void updateField(Integer organizerId, Integer fieldId, FieldDTO fieldDTO, MultipartFile photoFile) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        if (!field.getOrganizer().getId().equals(organizerId)) {
            throw new ApiException("You are not allowed to update another organizer's data");
        }

        field.setName(fieldDTO.getName());
        field.setDescription(fieldDTO.getDescription());
        field.setAddress(fieldDTO.getAddress());
        field.setOpen_time(fieldDTO.getOpen_time());
        field.setClose_time(fieldDTO.getClose_time());

        if (photoFile != null && !photoFile.isEmpty()) {
            deleteImage(field.getPhoto());
            String newPhoto = saveImage(photoFile);
            field.setPhoto(newPhoto);
        }

        fieldRepository.save(field);
    }

    // 13. Taha - Deletes an image file from the "uploads" directory - Tested
    private void deleteImage(String fileName) {
        try {
            // Build the full path to the file using the uploads folder and the file name
            Path filePath = Paths.get("uploads", fileName);

            // Check if the file actually exists
            if (Files.exists(filePath)) {
                // Delete the file from the file system
                Files.delete(filePath);
            }
        } catch (IOException e) {
            // If something goes wrong, print the stack trace and throw an API error
            e.printStackTrace();
            throw new ApiException("Failed to delete old image");
        }
    }

    public void deleteField(Integer organizerId, Integer fieldId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        if (!field.getOrganizer().getId().equals(organizerId)) {
            throw new ApiException("You are not allowed to delete another organizer's field");
        }

        deleteImage(field.getPhoto()); // Optional cleanup
        fieldRepository.delete(field);
    }

    // 59. Eatzaz -Show Details stadiums by sport type - Tested
    public List<NameCityFieldDTO> getFieldBySportAndAddress(Integer playerId, Integer sportId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("User not found");
        }
        Sport sport = sportRepository.findSportById(sportId);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        String city = player.getUser().getAddress();
        List<Field> fields = fieldRepository.findAllBySportIdAndLocation(sportId, city);
        if (fields.isEmpty()) {
            throw new ApiException("No fields found for this sport in your city");
        }
        List<NameCityFieldDTO> dtoList = new ArrayList<>();
        for (Field field : fields) {
            NameCityFieldDTO dto = new NameCityFieldDTO();
            dto.setName(field.getName());
            dto.setAddress(field.getAddress());
            dto.setPhoto(field.getPhoto());
            dtoList.add(dto);
        }
        return dtoList;
    }

    // 25. Eatzaz - Show Details stadiums by sport type - Tested
    public List<Field> getDetailsFieldBySportAndAddress(Integer playerId, Integer fieldId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("User not found");
        }
        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }
        Integer sportId = field.getSport().getId();
        String city = player.getUser().getAddress();
        List<Field> fields = fieldRepository.findAllBySportIdAndLocation(sportId, city);
        if (fields.isEmpty()) {
            throw new ApiException("No fields found for this sport in your city");
        }
        return fields;
    }

    // 41. Faisal - Assign field for private match - Tested
    public void playerChoseAFieldForPrivateMatch(Integer userId, Integer privateMatchId, Integer fieldId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("User not found or incorrect role");

        PrivateMatch privateMatch = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (privateMatch == null || !privateMatch.getPlayer().getId().equals(player.getId()))
            throw new ApiException("Private match not found or does not belong to this player");

        if (!privateMatch.getStatus().equals("CREATED"))
            throw new ApiException("You can only assign a field when the match is in CREATED status");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null)
            throw new ApiException("Field not found");

        String playerCity = player.getUser().getAddress();
        if (!field.getAddress().equals(playerCity))
            throw new ApiException("Field is not in the same city as the player");

        privateMatch.setField(field);
        privateMatch.setStatus("FIELD_ASSIGNED");
        privateMatchRepository.save(privateMatch);
    }

    // 14. Taha - Get Fields for an organizer - Tested
    public List<Field> getAllOrganizerFields(Integer userId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
        return fieldRepository.findFieldByOrganizer(organizer);
    }

    // 15. Taha - Returns a list of booked time slots for the specified field and date - Tested
    public List<TimeSlot> getBookedTimeSlotsForField(Integer userId, Integer fieldId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));
        if (!field.getOrganizer().getId().equals(organizer.getId())) {
            throw new ApiException("Unauthorized access");
        }
        List<TimeSlot> bookedSlots = timeSlotRepository.findByFieldAndStatus(field, "BOOKED");
        if (bookedSlots.isEmpty()) {
            throw new ApiException("No booked slots found for the given field");
        }
        return bookedSlots;
    }

    // 16 - Taha - Returns available (free) time slots for a field on the specified date - Tested
    public List<TimeSlot> getAvailableTimeSlots(Integer userId, Integer fieldId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));
        if (!field.getOrganizer().getId().equals(organizer.getId())) {
            throw new ApiException("Unauthorized access to this field");
        }
        List<TimeSlot> availableSlots = timeSlotRepository.findByFieldAndStatus(field, "AVAILABLE");
        if (availableSlots.isEmpty()) {
            throw new ApiException("No available slots found for this field. Create new slots.");
        }
        return availableSlots;
    }


}