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
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final TimeSlotService timeSlotService;
    private final TimeSlotRepository timeSlotRepository;

    public List<Field> getAllFields(){
        return fieldRepository.findAll();
    }

    // 40. Faisal - Get field by id - Tested
    public Field getFieldById(Integer id){
        return fieldRepository.findFieldById(id);
    }

    // 10. Taha - Public method to allow an approved organizer to add a new field with an image - Tested
    public void addField(Integer organizer_id, Integer sport_id, FieldDTO fieldDTO, MultipartFile photoFile) {
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
        if (!organizer.getStatus().equals("ACTIVE")) {
            throw new ApiException("Your account is not yet approved");
        }

        Sport sport = sportRepository.findSportById(sport_id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }

        String photo = saveImage(photoFile);
        Field field = new Field(
                null,
                fieldDTO.getName(),
                fieldDTO.getAddress(),
                fieldDTO.getDescription(),
                photo,
                fieldDTO.getOpen_time(),
                fieldDTO.getClose_time(),
                fieldDTO.getCapacity(),
                fieldDTO.getPrice(),
                sport,
                organizer,
                null,
                null,
                null,
                null);
        fieldRepository.save(field);
        timeSlotService.createFullDayTimeSlots(field.getId(), LocalDate.now());
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
    public void updateField(Integer organizer_id, Integer fieldId, FieldDTO fieldDTO, MultipartFile photoFile) {
        // Fetch the field from the database using its ID
        Field field = fieldRepository.findFieldById(fieldId);
        // If the field doesn't exist, throw an error
        if (field == null) {
            throw new ApiException("Field not found");
        }

        // Check that the field belongs to the organizer who's trying to update it
        if (!field.getOrganizer().getId().equals(organizer_id)) {
            throw new ApiException("You are not allowed to update another organizer's data");
        }

        // Update field's basic information from the DTO (name, description, etc.)
        field.setName(fieldDTO.getName());
        field.setDescription(fieldDTO.getDescription());
        field.setPhoto(field.getPhoto());
        field.setAddress(fieldDTO.getAddress());
        field.setOpen_time(fieldDTO.getOpen_time());
        field.setClose_time(fieldDTO.getClose_time());

        // If a new photo was uploaded
        if (photoFile != null && !photoFile.isEmpty()) {
            // Delete the old photo file from disk
            deleteImage(field.getPhoto());

            // Save the new image to disk and set its filename on the field
            String newPhoto = saveImage(photoFile);
            field.setPhoto(newPhoto);
        }

        // Save the updated field object to the database
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

    public void deleteField(Integer organizer_id, Integer fieldId){
        Field field= fieldRepository.findFieldById(fieldId);
        if (field== null){
            throw new ApiException("Field not found");
        }
        if (!field.getOrganizer().getId().equals(organizer_id)){
        throw new ApiException("You are not allowed to delete another organizer's data");
        }
        fieldRepository.delete(field);
    }
    //
// Eatzaz -Show Details stadiums by sport type - Tested
public List<NameCityFieldDTO> getFieldBySportAndCity(Integer player_id, Integer sportId){
    Player player = playerRepository.findPlayerById(player_id);
    if (player == null) {
        throw new ApiException("User Not Found");
    }

    Sport sport = sportRepository.findSportById(sportId);
    if (sport == null) {
        throw new ApiException("Sport not found");
    }

    List<Field> fields = fieldRepository.findAllBySportIdAndLocation(sportId, player.getUser().getAddress());
    if (fields.isEmpty()) {
        throw new ApiException("No fields found for this sport in your city");
    }

    List<NameCityFieldDTO> nameCityFieldDTOList = new ArrayList<>();

    for (Field field : fields) {
        NameCityFieldDTO dto = new NameCityFieldDTO();
        dto.setName(field.getName());
        dto.setAddress(field.getAddress());
        dto.setPhoto(field.getPhoto());

        nameCityFieldDTOList.add(dto);
    }
    return nameCityFieldDTOList;
}
    // 25. Eatzaz - Show Details stadiums by sport type - Tested
    public List<Field> getDetailsFieldBySportAndCity(Integer player_id, Integer fieldId) {
        Player player=playerRepository.findPlayerById(player_id);
        if(player==null)
            throw new ApiException("User Not Found");
Field field=fieldRepository.findFieldById(fieldId);
    if (field == null)
            throw new ApiException("Field not found");

        List<Field> fields = fieldRepository.findAllBySportIdAndLocation(field.getSport().getId(), player.getUser().getAddress());
        if (fields.isEmpty())
            throw new ApiException("No fields found for this sport in your city");
        return fields;
    }

    // 27. Eatzaz - Player Chose Field For Public Match - Tested
    public void playerChoseAFieldForAPublicMatch(Integer sport_Id,Integer playerId, Integer field_Id) {
        Player player = playerRepository.findPlayerById(playerId);

        if (player == null) {
            throw new ApiException("Player Not Found");
        }
        Field field = fieldRepository.findFieldById(field_Id);
        if (field == null) {
            throw new ApiException("Field Not Found");
        }
        Sport sport = sportRepository.findSportById(sport_Id);
        if (sport == null) {
            throw new ApiException("Sport Not Found");
        }

        if (!field.getAddress().equals(player.getUser().getAddress()) ||
                !field.getSport().getName().equals(sport.getName())) {
            if (!field.getAddress().equals(player.getUser().getAddress()) ||
                    !field.getSport().getId().equals(sport_Id))
                throw new ApiException("Field does not match player's city or sport");
            PublicMatch publicMatch = new PublicMatch();
            publicMatch.setField(field);
        }
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
       Organizer organizer= organizerRepository.findOrganizerById(userId);
        if (organizer==null) {
            throw new ApiException("You are not allowed to view another organizer's fields");
        }
        return fieldRepository.findFieldByOrganizer_Id(organizer.getId());
    }

    // 15. Taha - Returns a list of booked time slots for the specified field and date - Tested
    public List<TimeSlot> getBookedTimeSlots(Integer fieldId, LocalDate date) {
        // Get the field from the database
        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        // Filter time slots by the given date and sort them by start time
        return field.getTime_slots().stream()
                .filter(ts -> ts.getDate().equals(date)) // Only slots on the requested date
                .sorted(Comparator.comparing(TimeSlot::getStart_time)) // Sort by start time
                .toList(); // Return as a list
    }

    // 16 - Taha - Returns available (free) time slots for a field on the specified date - Tested
    public List<TimeSlot> getAvailableTimeSlots(Integer fieldId, LocalDate date) {
        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        // Get the booked slots on the given date, sorted by start time
        List<TimeSlot> bookedSlots = field.getTime_slots().stream()
                .filter(ts -> ts.getDate().equals(date))
                .sorted(Comparator.comparing(TimeSlot::getStart_time))
                .toList();

        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime openTime = field.getOpen_time();
        LocalTime closeTime = field.getClose_time();

        // Handle cases where closing time is after midnight
        boolean crossesMidnight = closeTime.isBefore(openTime);
        LocalTime current = openTime;

        // Loop through booked slots and find the gaps between them
        for (TimeSlot slot : bookedSlots) {
            if (current.isBefore(slot.getStart_time())) {
                // Add a free slot before the current booked slot
                availableSlots.add(new TimeSlot(
                        null, date, current, slot.getStart_time(), null,
                        "AVAILABLE", field, null, null
                ));
            }
            current = slot.getEnd_time(); // Move pointer forward
        }

        // Add the final available time after the last booked slot
        if (crossesMidnight) {
            // Add slot before midnight
            if (current.isBefore(LocalTime.MIDNIGHT)) {
                availableSlots.add(new TimeSlot(
                        null, date, current, LocalTime.MIDNIGHT, null,
                        "AVAILABLE", field, null, null
                ));
            }
            // Add slot after midnight on the next day
            if (LocalTime.MIN.isBefore(closeTime)) {
                availableSlots.add(new TimeSlot(null, date.plusDays(1), LocalTime.MIN, closeTime, null,
                        "AVAILABLE", field, null, null
                ));
            }
        } else {
            // Normal case: add final slot before closing time
            if (current.isBefore(closeTime)) {
                availableSlots.add(new TimeSlot(null, date, current, closeTime, null,
                        "AVAILABLE", field, null, null
                ));
            }
        }
        return availableSlots;
    }

}

