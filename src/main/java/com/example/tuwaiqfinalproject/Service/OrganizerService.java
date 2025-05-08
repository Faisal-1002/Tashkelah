package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.OrganizerDTO;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.AuthRepository;
import com.example.tuwaiqfinalproject.Repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final AuthRepository authRepository;

    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findAll();
    }

    public Organizer getOrganizer(Integer userId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        return organizer;
    }


    public Organizer getOrganizerById(Integer organizerId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        return organizer;
    }

    public void registerOrganizer(OrganizerDTO dto) {
        dto.setRole("ORGANIZER");
        String hashedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());

        User user = new User(null, dto.getUsername(), hashedPassword, dto.getRole(),
                dto.getName(), dto.getPhone(), dto.getCity(), dto.getEmail(), null, null);

        Organizer organizer = new Organizer(null, dto.getLicenceNumber(), dto.getStatus(), user, null, null);

        authRepository.save(user);
        organizerRepository.save(organizer);
    }

    public void updateOrganizer(Integer organizerId, OrganizerDTO dto) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        User user = organizer.getUser();
        user.setUsername(dto.getUsername());
        user.setPassword(new BCryptPasswordEncoder().encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setCity(dto.getCity());

        organizer.setLicenceNumber(dto.getLicenceNumber());
        organizer.setStatus(dto.getStatus());

        authRepository.save(user);
        organizerRepository.save(organizer);
    }

    public void deleteOrganizer(Integer organizerId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        authRepository.delete(organizer.getUser());
        organizerRepository.delete(organizer);
    }
}
