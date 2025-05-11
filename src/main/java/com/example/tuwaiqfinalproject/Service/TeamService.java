package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.Team;
import com.example.tuwaiqfinalproject.Repository.PublicMatchRepository;
import com.example.tuwaiqfinalproject.Repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final TeamRepository teamRepository;
    private final PublicMatchRepository publicMatchRepository;

public List<Team>getAllTeam(){
    return teamRepository.findAll();
}

    public void addTeamA(Integer publicMatchId, Team team){
        PublicMatch publicMatch= publicMatchRepository.findPublicMatchById(publicMatchId);
        if(publicMatch== null){
            throw new ApiException("PublicMatch not found");
        }
        if (publicMatch.getField() == null) {
            throw new ApiException("Field not assigned to this PublicMatch");
        }
        team.setMax_players_count(publicMatch.getField().getCapacity()/2);
        team.setPublic_match(publicMatch);
        teamRepository.save(team);
        publicMatch.setTeam(team);
        publicMatchRepository.save(publicMatch);
    }

    public void updateTameA(PublicMatch publicMatch, Integer tameAId, Team team) {

        Team oldTeam = teamRepository.findTeamAById(tameAId);
        if (oldTeam == null) {
            throw new ApiException("TeamA not found");

        }

        if (!oldTeam.getPublic_match().getId().equals(publicMatch.getId())) {
            throw new ApiException("You are not allowed to update another Team data");
        }
        oldTeam.setName(team.getName());
        oldTeam.setPlayersCount(team.getPlayersCount());
        teamRepository.save(oldTeam);
    }

    public void deleteTeamA(PublicMatch publicMatch, Integer teamAId){

        Team team = teamRepository.findTeamAById(teamAId);

        if (team ==null){
            throw new ApiException("Team not found");
        }
        if (!team.getPublic_match().getId().equals(publicMatch.getId()));
        throw new ApiException("You are not allowed to delete another team data");

    }
}
