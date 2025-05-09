package com.example.tuwaiqfinalproject.Service;


import com.example.tuwaiqfinalproject.DTO.GroupEmailDTO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailsService {



    private final JavaMailSender emailSender;

    //Taha - Emails to friends
    public void sendBulkEmail(String[] emails, GroupEmailDTO groupEmailDTO) {
        for (String recipient : emails) {
            try {
                MimeMessage message = emailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true);

                helper.setTo(recipient);
                helper.setSubject(groupEmailDTO.getSubject());
                helper.setText(groupEmailDTO.getMessageBody(), true);

                emailSender.send(message);
            } catch (MessagingException e) {
                // Skip failed email and continue sending to others
                System.out.println("Failed to send to: " + recipient);
            }
        }
    }

}
