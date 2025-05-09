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
    private final PublicMatchRepository publicMatchRepository;
    private final AuthRepository authRepository;
    private final PlayerRepository playerRepository;
    private final PrivateMatchRepository privateMatchRepository;

    public List<Field> getAllFields(){
        return fieldRepository.findAll();
    }

    private String saveImage(MultipartFile file) {
        if (file.isEmpty()) {
            throw new ApiException("No file selected");
        }

        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new ApiException("Invalid file type. Only images are allowed.");
        }

        try {
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path path = Paths.get("uploads/" + fileName);
            Files.write(path, file.getBytes());
            return fileName;
        } catch (IOException e) {
            throw new ApiException("Failed to save image");
        }
    }

    public void addField(Integer organizer_id, Integer sport_id, FieldDTO fieldDTO, MultipartFile photoFile) {
        Organizer organizer = organizerRepository.findOrganizerById(organizer_id);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }

        Sport sport = sportRepository.findSportById(sport_id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }

        String photo = saveImage(photoFile);

        Field field = new Field(null, fieldDTO.getName(), fieldDTO.getLocation(), fieldDTO.getDescription(), photo, fieldDTO.getOpenTime(), fieldDTO.getCloseTime(), fieldDTO.getCapacity(), organizer, sport, null, null, null);

        fieldRepository.save(field);
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
//        field.setPhoto(field.getPhoto());
        field.setLocation(fieldDTO.getLocation());
        field.setOpenTime(fieldDTO.getOpenTime());
        field.setCloseTime(fieldDTO.getCloseTime());

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
    // اظهار الملاعب للكل على حسب المدينه + الرياضه
//    public List<Field> getFieldBySportAndCity(String location,String sportName){
//        Sport sport=sportRepository.findSportByName(sportName);
//        List<Field> field=fieldRepository.findFieldByLocation(location);
//        if(sport==null){
//            throw new ApiException("Sport Not Found");
//        }
//        if(field==null){
//            throw new ApiException("Field Not Found");
//        }
//        for(Field f:field){
//            if( f.getLocation().equals(location) && f.getSport().getName().equals(sportName)){
////            throw new ApiException("There is no stadium for this sport in your city at the moment.");
//                return field;
//            }
//        }
//        return null;
//    }

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
    //اختيار ملعب
    public void playerChoseAField(String sportName,Integer playerId, Integer fieldId){
        Player player=playerRepository.findPlayerById(playerId);
        Field field= fieldRepository.findFieldById(fieldId);
        Sport sport=sportRepository.findSportByName(sportName);
      
        if(player==null){
            throw new ApiException("Player Not Found");
        }

        if (field == null) {
            throw new ApiException("Field Not Found");
        }
        if (sport == null) {
            throw new ApiException("Field Not Found");
        }

        if (!field.getLocation().equals(player.getUser().getCity()) ||
                !field.getSport().getName().equals(sportName)) {
            throw new ApiException("Field does not match player's city or sport");
        }
        PublicMatch publicMatch = new PublicMatch();
        publicMatch.setField(field);
        publicMatch.setPlayer(player);
    }
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
}
