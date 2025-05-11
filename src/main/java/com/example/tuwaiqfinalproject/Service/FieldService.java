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
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FieldService {

    private final FieldRepository fieldRepository;
    private final OrganizerRepository organizerRepository;
    private final SportRepository sportRepository;
    private final AuthRepository authRepository;
    private final PlayerRepository playerRepository;
    private final PrivateMatchRepository privateMatchRepository;

    public List<Field> getAllFields(){
        return fieldRepository.findAll();
    }

    // Taha - Upload image for a field
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

    //Taha--------------
    // Public method to allow an approved organizer to add a new field with an image
    public void addField(Integer organizer_id, Integer sport_id, FieldDTO fieldDTO, MultipartFile photoFile) {
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
//        if (!organizer.getStatus()) {
//            throw new ApiException("Your account is not yet approved");
//        }

        Sport sport = sportRepository.findSportById(sport_id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }

        String photo = saveImage(photoFile);
        Field field = new Field(null, fieldDTO.getName(), fieldDTO.getAddress(), fieldDTO.getDescription(), photo, fieldDTO.getOpen_time(), fieldDTO.getClose_time(), fieldDTO.getCapacity(),sport, organizer,null, null, null);
        fieldRepository.save(field);

//
//        String photo = saveImage(photoFile);
//        Field field = new Field(
//                null,
//                fieldDTO.getName(),
//                fieldDTO.getAddress(),
//                fieldDTO.getDescription(),
//                photo,
//                fieldDTO.getOpen_time(),
//                fieldDTO.getClose_time(),
//                fieldDTO.getCapacity(),
//                sport,
//                organizer,
//                null,
//                null,
//                null);
//        fieldRepository.save(field);
    }

    public void updateField(Integer organizer_id, Integer fieldId, FieldDTO fieldDTO){
        Field field= fieldRepository.findFieldById(fieldId);

        if(field == null){
            throw new ApiException("Filed not found");

        }
        if(!field.getOrganizer().getId().equals(organizer_id)){
            throw new ApiException("You are not allowed to update another organizer's data");

        }

        field.setName(fieldDTO.getName());
        field.setDescription(fieldDTO.getDescription());
        field.setPhoto(field.getPhoto());
        field.setAddress(fieldDTO.getAddress());
        field.setOpen_time(fieldDTO.getOpen_time());
        field.setClose_time(fieldDTO.getClose_time());

        fieldRepository.save(field);
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


//    // 1- Eatzaz - Show stadiums by sport type -tested
    public List<Field> getFieldBySportAndCity(Integer player_id, Integer sportId) {
        Player player=playerRepository.findPlayerById(player_id);
        if(player==null)
            throw new ApiException("User Not Found");
        Sport sport = sportRepository.findSportById(sportId);
        if (sport == null)
            throw new ApiException("Sport not found");

        List<Field> fields = fieldRepository.findAllBySportIdAndLocation(sportId, player.getUser().getAddress());
        if (fields.isEmpty())
            throw new ApiException("No fields found for this sport in your city");

        return fields;
    }
    //2- Eatzaz - player Chose Field For Public Match - tested
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
        // 24. Faisal - Assign field for private match - Tested
//        public void playerChoseAFieldForPrivateMatch (Integer user_id, Integer fieldId){
//            Player player = playerRepository.findPlayerById(user_id);
//            if (player == null) throw new ApiException("User not found or incorrect role");
//
//            PrivateMatch privateMatch = player.getPrivateMatch();
//            if (privateMatch == null)
//                throw new ApiException("Private match not found");
//
//            if (!privateMatch.getStatus().equals("PENDING"))
//                throw new ApiException("You can only assign a field when the match is in PENDING status");
//
//            Field field = fieldRepository.findFieldById(fieldId);
//            if (field == null) throw new ApiException("Field not found");
//
//            // Use player's city directly (from User inside Player)
//            String playerCity = player.getUser().getCity();
//            if (!field.getLocation().equals(playerCity))
//                throw new ApiException("Field is not in the same city as the player");
//
//            privateMatch.setField(field);
//            privateMatch.setStatus("SCHEDULED");
//            privateMatchRepository.save(privateMatch);
//        }

        // Taha - Get all organizer fields
        public List<Field> getAllOrganizerFields (Integer userId){
            Organizer organizer = organizerRepository.findOrganizerById(userId);
            if (organizer == null) {
                throw new ApiException("You are not allowed to view another organizer's fields");
            }

            return fieldRepository.findFieldByOrganizer_Id(organizer.getId());
        }

    }

