package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.FieldRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import com.example.tuwaiqfinalproject.Repository.PublicMatchRepository;
import com.example.tuwaiqfinalproject.Repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PublicMatchService {

    private final PublicMatchRepository publicMatchRepository;
    private final PlayerRepository playerRepository;
    private final SportRepository sportRepository;
    private final FieldRepository fieldRepository;

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
    // اختيار فريق عشوائي
    public void PlayWithPublicTeam(Integer sportId,Integer fieldId,Integer playerId){
        Player player=playerRepository.findPlayerById(playerId);
        Sport sport=sportRepository.findSportById(sportId);
        Field field=fieldRepository.findFieldById(fieldId);
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(player.getPrivateMatch().getId());
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        publicMatchRepository.save(publicMatch);
    }
    // عرض المباريات + الفرق
    public PublicMatch getMatchAndTeam(Integer playerId, Integer sportId, Integer fieldId,Integer publicMatchId){
        Player player=playerRepository.findPlayerById(playerId);
        Sport sport=sportRepository.findSportById(sportId);
        Field field=fieldRepository.findFieldById(fieldId);
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(publicMatchId);
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        if(publicMatch==null){
            throw new ApiException("Field Not Found");
        }
        return publicMatch;
    }

    // اختيار فريق
    public void selectTeam(Integer playerId,Integer sportId,Integer fieldId){
        Player player=playerRepository.findPlayerById(playerId);
        Sport sport=sportRepository.findSportById(sportId);
        Field field=fieldRepository.findFieldById(fieldId);
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(player.getPublicMatch().getId());
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        if(publicMatch==null){
            throw new ApiException("Field Not Found");
        }
        if(player.getPublicMatch().getTeamA().equals(publicMatch.getTeamA())){
            publicMatch.setPlayer(player);
            TeamA teamA = publicMatch.getTeamA();
            teamA.setPlayersCount(teamA.getPlayersCount()-1);
            publicMatch.setTeamA(teamA);
        }
        publicMatch.setPlayer(player);
        publicMatch.setTeamB(publicMatch.getTeamB());
        TeamB teamB=publicMatch.getTeamB();
        teamB.setPlayersCount(teamB.getPlayersCount()-1);
    }
}
