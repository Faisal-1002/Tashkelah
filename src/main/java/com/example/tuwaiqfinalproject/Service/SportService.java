package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.Sport;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import com.example.tuwaiqfinalproject.Repository.SportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SportService {

    private final SportRepository sportRepository;
    private final PlayerRepository playerRepository;

    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    public Sport getSportById(Integer id) {
        Sport sport = sportRepository.findSportById(id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        return sport;
    }

    public void addSport(Sport sport) {
        sportRepository.save(sport);
    }

    public void updateSport(Integer id, Sport updatedSport) {
        Sport sport = sportRepository.findSportById(id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        sport.setName(updatedSport.getName());
        sport.setDefaultPlayerCount(updatedSport.getDefaultPlayerCount());
        sportRepository.save(sport);
    }

    public void deleteSport(Integer id) {
        Sport sport = sportRepository.findSportById(id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        sportRepository.delete(sport);
    }
    // Eatzaz - Choose a sport
    public void ChooseSport(Integer playerId, Integer sportId,String sportName) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Sport not found");
        }
        Sport sport = sportRepository.findSportById(sportId);
        if (sport == null) {
            throw new ApiException("Sport not found");}
        Sport sportByName=sportRepository.findSportByName(sportName);
        if(! sport.getName().equals(sportName)){

        }
        sportRepository.save(sportByName);
    }
}
