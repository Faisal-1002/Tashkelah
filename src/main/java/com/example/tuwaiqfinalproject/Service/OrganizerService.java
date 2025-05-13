package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.OrganizerDTO;
import com.example.tuwaiqfinalproject.Model.Organizer;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Repository.AuthRepository;
import com.example.tuwaiqfinalproject.Repository.OrganizerRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizerService {

    private final OrganizerRepository organizerRepository;
    private final AuthRepository authRepository;
    private final EmailsService emailsService;
    private final JavaMailSender mailSender;

    public List<Organizer> getAllOrganizers() {
        return organizerRepository.findAll();
    }

    // 55. Faisal - Get Organizer info - Tested
    public Organizer getOrganizer(Integer userId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        return organizer;
    }

    // 56. Faisal - Get Organizer by id - Tested
    public Organizer getOrganizerById(Integer organizerId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        return organizer;
    }

    // 1. Taha - Register organizer - Tested
    public void registerOrganizer(OrganizerDTO dto) {
        String hashedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
        User user = new User(null, dto.getUsername(), hashedPassword, "ORGANIZER",
                dto.getName(), dto.getPhone(), dto.getAddress(), dto.getEmail(), null, null);
        Organizer organizer = new Organizer(null, dto.getLicence_number(), "INACTIVE", user, null, null);

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
        user.setAddress(dto.getAddress());
        organizer.setLicence_number(dto.getLicence_number());

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

    // 2. Taha - Admin approve organizer - Tested
    public void approveOrganizer(Integer organizerId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        organizer.setStatus("ACTIVE");
        organizerRepository.save(organizer);
        sendApprovalEmail(organizer);
    }

    // 3. Taha - Reject Organizer - Tested
    public void rejectOrganizer(Integer organizerId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        organizer.setStatus("INACTIVE");
        organizerRepository.save(organizer);
        sendRejectedEmail(organizer);
    }

    // 4. Taha - Block Organizer - Tested
    public void blockOrganizer(Integer organizerId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null)
            throw new ApiException("Organizer not found");
        organizer.setStatus("BLOCKED");
        organizerRepository.save(organizer);
        sendBlockedEmail(organizer);
    }

    // 5. Taha - Send approve notification to organizer - Tested
    private void sendApprovalEmail(Organizer organizer) {
        //  Define the subject of the email
        String subject = "Your Account Has Been Approved!";

        //  Write the body message of the email
        String message = "🎉 Congratulations!\n\n" +
                "Your account has been approved successfully.\n" +
                "You can now start adding your fields and managing your activities.\n\n" +
                "Best of luck on your journey!";
        //  Send the email with the embedded image
        sendEmailWithImage(organizer.getUser().getEmail(), subject, message);
    }

    // 5. Taha - Send approve notification WithImage to organizer - Tested
    @Value("${email.logo.path}")
    private String logoPath;
    private void sendEmailWithImage(String to, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true); // true to enable attachments

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("finalproject.taha@gmail.com");

            // HTML body with larger image
            String html = "<html><body>" +
                    "<img src='cid:logo' style='width:300px; height:auto;'><br>" +
                    "<p>🎉 Congratulations!<br><br>" +
                    "Your account has been approved successfully.<br>" +
                    "You can now start adding your fields and managing your activities.<br><br>" +
                    "Best of luck on your journey 👏🏼 👏🏼 👏🏼!</p>" +
                    "</body></html>";

            // Set the HTML content
            helper.setText(html, true); // true = isHtml

            // Load the image from the classpath using the path from application.properties
            ClassPathResource logo = new ClassPathResource(logoPath);
            helper.addInline("logo", logo);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            throw new ApiException("Email Error: " + e.getMessage());
        }
    }

    // 6. Taha - Send reject notification to organizer - Tested
    private void sendRejectedEmail(Organizer organizer) {
        String subject = "Your Account Has Been Rejected";
        String message = "😔 We’re sorry to inform you that your account has been rejected.\n\n" +
                "Please make sure all your information is correct and try again.\n" +
                "If you need help, feel free to reach out to us.\n\n" +
                "We’re here to support you.";

        emailsService.sendEmail(organizer.getUser().getEmail(), subject, message);
    }

    // 7. Taha - Send block notification to organizer - Tested
    private void sendBlockedEmail(Organizer organizer) {
        String subject = "Your Account Has Been Blocked";
        String message = "🚫 Your account has been blocked due to policy violations or other reasons.\n\n" +
                "Please contact support if you believe this is a mistake or require assistance.";

        emailsService.sendEmail(organizer.getUser().getEmail(), subject, message);
    }

}