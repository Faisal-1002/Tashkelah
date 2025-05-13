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

    // 53. Faisal - Add email to private match - Tested
    public void addEmailToPrivateMatch(Integer userId, Integer privateMatchId, Emails email) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        PrivateMatch privateMatch = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (privateMatch == null) {
            throw new ApiException("Private match not found");
        }
        if (!privateMatch.getPlayer().equals(player))
            throw new ApiException("Private match does not belong to this player");
        email.setPrivate_match(privateMatch);
        emailsRepository.save(email);
    }

    // 61. Faisal - Get emails for my private match - Tested
    public List<Emails> getEmailsForPrivateMatch(Integer userId, Integer privateMatchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        PrivateMatch privateMatch = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (privateMatch == null)
            throw new ApiException("Private match not found");
        if (!privateMatch.getPlayer().equals(player))
            throw new ApiException("Private match does not belong to this player");
        return emailsRepository.findAllByPrivateMatchId(privateMatchId);
    }

    public void deleteEmail(Integer userId, Integer privateMatchId, Integer emailId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PrivateMatch privateMatch = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (privateMatch == null)
            throw new ApiException("Private match not found");

        if (!privateMatch.getPlayer().getId().equals(player.getId()))
            throw new ApiException("Private match does not belong to this player");

        Emails email = emailsRepository.findEmailsById(emailId);
        if (email == null)
            throw new ApiException("Email not found");

        if (!privateMatch.getEmails().contains(email))
            throw new ApiException("This email is not part of the specified private match");

        emailsRepository.delete(email);
    }

    // 54. Faisal - Send invites - Tested
    public void sendInvites(Integer userId, Integer privateMatchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null) throw new ApiException("Player not found");
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (match == null || !match.getStatus().equals("CONFIRMED"))
            throw new ApiException("Private match must be confirmed before sending invites");
        if (!match.getPlayer().equals(player))
            throw new ApiException("Private match does not belong to this player");
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

    }

    // 55. Faisal - Email notification - Tested
    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("finalproject.taha@gmail.com");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
