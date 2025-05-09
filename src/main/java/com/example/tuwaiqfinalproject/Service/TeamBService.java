package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import com.example.tuwaiqfinalproject.Model.TeamB;
import com.example.tuwaiqfinalproject.Repository.PublicMatchRepository;
import com.example.tuwaiqfinalproject.Repository.TeamBRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TeamBService {


    private final TeamBRepository teamBRepository;
    private final PublicMatchRepository publicMatchRepository;

    public void addTeamB(Integer publicMatchId, TeamB teamB){

        PublicMatch publicMatch= publicMatchRepository.findPublicMatchById(publicMatchId);
        if(publicMatch== null){
            throw new ApiException("PublicMatch not found");
        }

        publicMatch.setTeamB(teamB);
    }

    public void updateTeamB(PublicMatch publicMatch, Integer tameB_id,TeamB teamB) {

        TeamB oldTeam = teamBRepository.findTeamBById(tameB_id);
        if (oldTeam == null) {
            throw new ApiException("TeamB not found");
        }

        if (!oldTeam.getPublicMatch().getId().equals(publicMatch.getId())) {
            throw new ApiException("You are not allowed to update another Team data");
        }
        oldTeam.setTeamName(teamB.getTeamName());
        oldTeam.setPlayersCount(teamB.getPlayersCount());
        teamBRepository.save(oldTeam);
    }

    public void deleteTeamB(PublicMatch publicMatch, Integer teamA_id){

        TeamB teamB= teamBRepository.findTeamBById(teamA_id);

        if (teamB==null){
            throw new ApiException("Team not found");
        }
        if (!teamB.getPublicMatch().getId().equals(publicMatch.getId())) {
            throw new ApiException("You are not allowed to delete another team data");
        }
        teamBRepository.delete(teamB);
    }
}
