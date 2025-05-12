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

    // 38. Faisal - Get private match by id - Tested
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

    // 39. Faisal - Choose a field and create a private match - Tested
    public void createPrivateMatch(Integer userId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        List<PrivateMatch> matches = privateMatchRepository.findPrivateMatchByPlayer(player);
        boolean hasUnconfirmed = matches.stream()
                .anyMatch(m -> !m.getStatus().equals("CONFIRMED"));

        if (hasUnconfirmed)
            throw new ApiException("You already have an active private match");

        PrivateMatch privateMatch = new PrivateMatch();
        privateMatch.setPlayer(player);
        privateMatch.setStatus("CREATED");

        privateMatchRepository.save(privateMatch);
    }

}
