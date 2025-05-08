package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Repository.PrivateMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateMatchService {

    private final PrivateMatchRepository privateMatchRepository;

    public List<PrivateMatch> getAllPrivateMatches() {
        return privateMatchRepository.findAll();
    }

    public PrivateMatch getPrivateMatchById(Integer id) {
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(id);
        if (match == null)
            throw new ApiException("Private match not found");
        return match;
    }

    public void addPrivateMatch(PrivateMatch match) {
        privateMatchRepository.save(match);
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
}
