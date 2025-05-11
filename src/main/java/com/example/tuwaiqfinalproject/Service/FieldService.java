package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.FieldDTO;
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
    private final PublicMatchRepository publicMatchRepository;
    private final AuthRepository authRepository;
    private final PlayerRepository playerRepository;
    private final PrivateMatchRepository privateMatchRepository;


    public List<Field> getAllFields(){
        return fieldRepository.findAll();
    }




    // Taha----------------- test-- (3)
// Private method to save an uploaded image file
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

    //Taha-------------- test-(4)
    // Public method to allow an approved organizer to add a new field with an image
    public void addField(Integer organizer_id, Integer sport_id, FieldDTO fieldDTO, MultipartFile photoFile) {
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
        if (!organizer.getStatus()) {
            throw new ApiException("Your account is not yet approved");
        }

        Sport sport = sportRepository.findSportById(sport_id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }

        String photo = saveImage(photoFile);
        Field field = new Field(null,fieldDTO.getName(),fieldDTO.getLocation(),fieldDTO.getDescription(),photo,fieldDTO.getOpenTime(),fieldDTO.getCloseTime(),
                fieldDTO.getCapacity(),organizer,null,sport,null,null);
        fieldRepository.save(field);
    }


    //Taha---------------//test (9)

    // Allows an organizer to update an existing field's information and photo
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
        field.setLocation(fieldDTO.getLocation());
        field.setOpenTime(fieldDTO.getOpenTime());
        field.setCloseTime(fieldDTO.getCloseTime());

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



    //Taha---------------// (10)
    // Deletes an image file from the "uploads" directory
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
   //  اظهار الملاعب للكل على حسب المدينه + الرياضه
    public List<Field> getFieldBySportAndCity(String location,String sportName){
        Sport sport=sportRepository.findSportByName(sportName);
        List<Field> field=fieldRepository.findFieldByLocation(location);
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        for(Field f:field){
            if( f.getLocation().equals(location) && f.getSport().getName().equals(sportName)){
//            throw new ApiException("There is no stadium for this sport in your city at the moment.");
                return field;
            }
        }
        return null;
    }

    // اظهار الملاعب للكل على حسب المدينه + الرياضه
    public List<Field> getFieldBySportAndCity(Integer user_id, String sportName) {
        User user=authRepository.findUserById(user_id);
        if(user==null)
            throw new ApiException("User Not Found");
        Sport sport = sportRepository.findSportByName(sportName);
        if (sport == null)
            throw new ApiException("Sport not found");
        List<Field> fields = fieldRepository.findAllBySportNameAndCity(sportName, user.getCity());
        if (fields.isEmpty())
            throw new ApiException("No fields found for this sport in your city");
        return fields;
    }

//    //اختيار ملعب
//    public void playerChoseAField(String sportName,Integer playerId, Integer fieldId){
//        User user=authRepository.findUserById(playerId);
//        Field field= fieldRepository.findFieldById(fieldId);
//        Sport sport=sportRepository.findSportByName(sportName);
//
//        if(user==null){
//            throw new ApiException("Player Not Found");
//        }
//
//        if (field == null) {
//            throw new ApiException("Field Not Found");
//        }
//        if (sport == null) {
//            throw new ApiException("Field Not Found");
//        }
//
//        if (!field.getLocation().equals(player.getUser().getCity()) ||
//                !field.getSport().getName().equals(sportName)) {
//            throw new ApiException("Field does not match player's city or sport");
//        }
//        PublicMatch publicMatch = new PublicMatch();
//        publicMatch.setField(field);
//        publicMatch.setPlayer(player);
//    }

    // 24. Faisal - Assign field for private match - Tested
    public void playerChoseAFieldForPrivateMatch(Integer user_id, Integer fieldId) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null) throw new ApiException("User not found or incorrect role");

        PrivateMatch privateMatch = player.getPrivateMatch();
        if (privateMatch == null)
            throw new ApiException("Private match not found");

        if (!privateMatch.getStatus().equals("PENDING"))
            throw new ApiException("You can only assign a field when the match is in PENDING status");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) throw new ApiException("Field not found");

        // Use player's city directly (from User inside Player)
        String playerCity = player.getUser().getCity();
        if (!field.getLocation().equals(playerCity))
            throw new ApiException("Field is not in the same city as the player");

        privateMatch.setField(field);
        privateMatch.setStatus("SCHEDULED");
        privateMatchRepository.save(privateMatch);
    }

    //Taha----------------------------------(5)
    public List<Field> getAllOrganizerFields(Integer userId) {
       Organizer organizer= organizerRepository.findOrganizerById(userId);
        if (organizer==null) {
            throw new ApiException("You are not allowed to view another organizer's fields");
        }

        return fieldRepository.findFieldByOrganizer_Id(organizer.getId());
    }




    // Taha - (7)Get booked time slots for a field on a specific date
    // Returns a list of booked time slots for the specified field and date
    public List<TimeSlot> getBookedTimeSlots(Integer fieldId, LocalDate date) {
        // Get the field from the database
        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        // Filter time slots by the given date and sort them by start time
        return field.getTimeSlots().stream()
                .filter(ts -> ts.getDate().equals(date)) // Only slots on the requested date
                .sorted(Comparator.comparing(TimeSlot::getStartTime)) // Sort by start time
                .toList(); // Return as a list
    }




    // Taha -(8) Get available time slots for a field on a specific date
    // Returns available (free) time slots for a field on the specified date
    public List<TimeSlot> getAvailableTimeSlots(Integer fieldId, LocalDate date) {
        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) {
            throw new ApiException("Field not found");
        }

        // Get the booked slots on the given date, sorted by start time
        List<TimeSlot> bookedSlots = field.getTimeSlots().stream()
                .filter(ts -> ts.getDate().equals(date))
                .sorted(Comparator.comparing(TimeSlot::getStartTime))
                .toList();

        List<TimeSlot> availableSlots = new ArrayList<>();
        LocalTime openTime = field.getOpenTime();
        LocalTime closeTime = field.getCloseTime();

        // Handle cases where closing time is after midnight
        boolean crossesMidnight = closeTime.isBefore(openTime);
        LocalTime current = openTime;

        // Loop through booked slots and find the gaps between them
        for (TimeSlot slot : bookedSlots) {
            if (current.isBefore(slot.getStartTime())) {
                // Add a free slot before the current booked slot
                availableSlots.add(new TimeSlot(
                        null, date, current, slot.getStartTime(), null,
                        "AVAILABLE", field, null, null
                ));
            }
            current = slot.getEndTime(); // Move pointer forward
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
