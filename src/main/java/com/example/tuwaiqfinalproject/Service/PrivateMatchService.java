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

    // 62. Faisal - Get my private matches - Tested
    public List<PrivateMatch> getMyPrivateMatches(Integer userId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        List<PrivateMatch> matches = player.getPrivate_matches();
        if (matches == null || matches.isEmpty())
            throw new ApiException("No private matches found for this player");
        return matches;
    }


    public void updatePrivateMatch(Integer userId, Integer privateMatchId, PrivateMatch updatedMatch) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (match == null)
            throw new ApiException("Private match not found");
        if (!match.getPlayer().getId().equals(player.getId()))
            throw new ApiException("Player id mismatch");

        updatedMatch.setId(match.getId());
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
