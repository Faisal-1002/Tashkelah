package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.Emails;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Repository.EmailsRepository;
import com.example.tuwaiqfinalproject.Repository.PrivateMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmailsService {

    private final EmailsRepository emailsRepository;
    private final PrivateMatchRepository privateMatchRepository;

    public void addEmailToPrivateMatch(Integer privateMatchId, Emails email) {
        PrivateMatch privateMatch = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (privateMatch == null) {
            throw new ApiException("Private match not found");
        }
        email.setPrivateMatch(privateMatch);
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
}
