package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.FieldDTO;
import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.Organizer;
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

    public void addField(Integer organizerId, FieldDTO fieldDTO, MultipartFile photoFile){

        Organizer organizer1= organizerRepository.findOrganizerById(organizerId);
        if(organizer1==null){

            throw new ApiException("Organizer not found");
        }

        String photo= saveImage(photoFile);

        Field field = new Field(null,fieldDTO.getName(),fieldDTO.getLocation(),fieldDTO.getDescription(),photo,fieldDTO.getOpenTime(),fieldDTO.getCloseTime(),fieldDTO.getCapacity(),organizer1,null);

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

}
