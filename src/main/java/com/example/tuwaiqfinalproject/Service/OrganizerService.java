package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.OrganizerDTO;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.AuthRepository;
import com.example.tuwaiqfinalproject.Repository.OrganizerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final AuthRepository authRepository;
    private final JavaMailSender mailSender;


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

        Organizer organizer = new Organizer(null, dto.getLicenceNumber(), false, user, null, null);

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






    //Taha----------------------// test-(2)
    public void approveOrganizer(Integer organizerId, Boolean isApproved) {
        Organizer organizer = organizerRepository.findById(organizerId)
                .orElseThrow(() -> new ApiException("Organizer not found"));

        // Set the approval status
        organizer.setStatus(isApproved);

        // Save the updated organizer entity to the database
        organizerRepository.save(organizer);

        // Send an approval or rejection email to the organizer
        sendApprovalEmail(organizer, isApproved);
    }

    //Taha----------------------- //test(3)
    private void sendApprovalEmail(Organizer organizer, Boolean isApproved) {
        // Email subject
        String subject = "License Approval Notification";

        // Email body based on approval status
        String message;
        if (isApproved) {
            message = "Congratulations! 🎉\n\n" +
                    "Your license has been approved successfully.\n" +
                    "You can now start adding your fields and managing your activities. Great job, champ! 💪\n\n" +
                    "Best of luck on your journey!";
        } else {
            message = "We’re sorry to inform you that your license has been rejected. 😔\n\n" +
                    "Please make sure all your information is correct and try again.\n" +
                    "If you need help, feel free to reach out to us.\n\n" +
                    "We’re here to support you!";
        }

        // Get the organizer's email
        String recipientEmail = organizer.getUser().getEmail();

        // Create and send the email
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(recipientEmail);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }




}
