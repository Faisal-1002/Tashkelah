package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Repository.PublicMatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicMatchService {

    private final PublicMatchRepository publicMatchRepository;

    public List<PublicMatch> getAllPublicMatches() {
        return publicMatchRepository.findAll();
    }

    public PublicMatch getPublicMatchById(Integer id) {
        PublicMatch match = publicMatchRepository.findPublicMatchById(id);
        if (match == null)
            throw new ApiException("Public match not found");
        return match;
    }

    public void addPublicMatch(PublicMatch match) {
        publicMatchRepository.save(match);
    }

    public void updatePublicMatch(Integer id, PublicMatch updatedMatch) {
        PublicMatch existing = publicMatchRepository.findPublicMatchById(id);
        if (existing == null)
            throw new ApiException("Public match not found");

        updatedMatch.setId(existing.getId());
        publicMatchRepository.save(updatedMatch);
    }

    public void deletePublicMatch(Integer id) {
        PublicMatch match = publicMatchRepository.findPublicMatchById(id);
        if (match == null)
            throw new ApiException("Public match not found");
        publicMatchRepository.delete(match);
    }
}
