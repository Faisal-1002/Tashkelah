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

    // 45. Faisal - Get all sports - Tested
    public List<Sport> getAllSports() {
        return sportRepository.findAll();
    }

    // 46. Faisal - Get sport by id - Tested
    public Sport getSportById(Integer id) {
        Sport sport = sportRepository.findSportById(id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        return sport;
    }

    // 9. Taha - Add sport - Tested
    public void addSport(Sport sport) {
        sportRepository.save(sport);
    }

    public void updateSport(Integer id, Sport updatedSport) {
        Sport sport = sportRepository.findSportById(id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        sport.setName(updatedSport.getName());
        sport.setDefault_player_count(updatedSport.getDefault_player_count());
        sportRepository.save(sport);
    }

    public void deleteSport(Integer id) {
        Sport sport = sportRepository.findSportById(id);
        if (sport == null) {
            throw new ApiException("Sport not found");
        }
        sportRepository.delete(sport);
    }
}
