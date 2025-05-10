package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.Emails;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Repository.EmailsRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import com.example.tuwaiqfinalproject.Repository.PrivateMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailsService {

    private final JavaMailSender mailSender;
    private final EmailsRepository emailsRepository;
    private final PrivateMatchRepository privateMatchRepository;
    private final PlayerRepository playerRepository;

    public void addEmailToPrivateMatch(Integer privateMatchId, Emails email) {
        PrivateMatch privateMatch = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (privateMatch == null) {
            throw new ApiException("Private match not found");
        }
        email.setPrivate_match(privateMatch);
        emailsRepository.save(email);
    }

    public List<Emails> getEmailsForPrivateMatch(Integer privateMatchId) {
        if (!privateMatchRepository.existsById(privateMatchId)) {
            throw new ApiException("Private match not found");
        }
        return emailsRepository.findAllByPrivateMatchId(privateMatchId);
    }

    public void deleteEmail(Integer emailId) {
        Emails email = emailsRepository.findEmailsById(emailId);
        if (email == null) {
            throw new ApiException("Email not found");
        }
        emailsRepository.delete(email);
    }

    // 29. Faisal - Send invites - Tested
    public void sendInvites(Integer userId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null) throw new ApiException("Player not found");

        PrivateMatch match = player.getPrivate_match();
        if (match == null || !match.getStatus().equals("CONFIRMED"))
            throw new ApiException("Private match must be confirmed before sending invites");

        List<Emails> invites = match.getEmails();
        if (invites == null || invites.isEmpty())
            throw new ApiException("No emails to send invites to");

        for (Emails emailEntry : invites) {
            String to = emailEntry.getEmail();
            String subject = "You're Invited to a Private Match!";
            String body = "Hey there!\n\nYou've been invited to join a private match: "
                    + "\nLocation: " + match.getField().getAddress()
                    + "\nOrganizer: " + player.getUser().getName();

           sendEmail(to, subject, body);
        }

        privateMatchRepository.save(match);
    }

    // 30. Faisal - Email notification - Tested
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("faisal.a.m.2012@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
