package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateMatchService {

    private final PrivateMatchRepository privateMatchRepository;
    private final PlayerRepository playerRepository;
    private final FieldRepository fieldRepository;

    public List<PrivateMatch> getAllPrivateMatches() {
        return privateMatchRepository.findAll();
    }

    public PrivateMatch getPrivateMatchById(Integer id) {
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(id);
        if (match == null)
            throw new ApiException("Private match not found");
        return match;
    }

    public void updatePrivateMatch(Integer id, PrivateMatch updatedMatch) {
        PrivateMatch existing = privateMatchRepository.findPrivateMatchById(id);
        if (existing == null)
            throw new ApiException("Private match not found");

        updatedMatch.setId(existing.getId());
        privateMatchRepository.save(updatedMatch);
    }

    public void deletePrivateMatch(Integer id) {
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(id);
        if (match == null)
            throw new ApiException("Private match not found");
        privateMatchRepository.delete(match);
    }

    // Faisal - Choose a field and create a private match
    public void createPrivateMatchWithField(Integer userId, Integer fieldId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        if (player.getPrivate_match() != null)
            throw new ApiException("Player already has a private match");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null)
            throw new ApiException("Field not found");

        if (!field.getAddress().equals(player.getUser().getAddress()))
            throw new ApiException("Field is not in the same city as the player");

        PrivateMatch privateMatch = new PrivateMatch();
        privateMatch.setField(field);
        privateMatch.setPlayer(player);
        privateMatch.setStatus("CREATED");

        privateMatchRepository.save(privateMatch);
    }
}
