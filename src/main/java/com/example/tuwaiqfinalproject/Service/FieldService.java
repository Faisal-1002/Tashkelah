package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.FieldDTO;
import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.FieldRepository;
import com.example.tuwaiqfinalproject.Repository.OrganizerRepository;
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

    public List<Field> getAllFields(){
        return fieldRepository.findAll();
    }




    // Taha-----------------
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




    //Taha--------------
    // Public method to allow an approved organizer to add a new field with an image
    public void addField(Organizer organizer, FieldDTO fieldDTO, MultipartFile photoFile) {

        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }
        if (!organizer.getStatus()) {
            throw new ApiException("Your account is not yet approved");
        }

        // Save the uploaded image and get the stored file name
        String photo = saveImage(photoFile);

        Field field = new Field(null,fieldDTO.getName(),fieldDTO.getLocation(),fieldDTO.getDescription(),photo,fieldDTO.getOpenTime(),fieldDTO.getCloseTime(),fieldDTO.getCapacity(),organizer,null,null);

        fieldRepository.save(field);
    }



    public void updateField(Organizer organizer, Integer fieldId, FieldDTO fieldDTO){
        Field field= fieldRepository.findFieldById(fieldId);

        if(field == null){
            throw new ApiException("Filed not found");

        }
        if(!field.getOrganizer().getId().equals(organizer.getId())){
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

    public void deleteField(Organizer organizer, Integer fieldId){

        Field field= fieldRepository.findFieldById(fieldId);

        if (field== null){
            throw new ApiException("Field not found");
        }

        if (!field.getOrganizer().getId().equals(organizer.getId())){

        throw new ApiException("You are not allowed to delete another organizer's data");
    }

        fieldRepository.delete(field);
    }

    //Taha----------------------------------
    public List<Field> getAllOrganizerFields(Integer userId) {
       Organizer organizer= organizerRepository.findOrganizerById(userId);
        if (organizer==null) {
            throw new ApiException("You are not allowed to view another organizer's fields");
        }

        return fieldRepository.findFieldByOrganizer_Id(organizer.getId());
    }

}
