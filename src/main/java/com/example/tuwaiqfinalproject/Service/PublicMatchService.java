package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PublicMatchDTO;
import com.example.tuwaiqfinalproject.DTO.Team_DTO;
import com.example.tuwaiqfinalproject.DTO.PlayerSelectionDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
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
    private final TeamRepository teamRepository;
    private final TimeSlotRepository timeSlotRepository;

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
        match.setStatus("OPEN");
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
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        if(field==null){
            throw new ApiException("Field Not Found");
        }
//        PublicMatch publicMatch = publicMatchRepository
//                .findBySportAndFieldAndStatus(sport, field, "Pending");
//        publicMatch.getPlayers().add(player);
//        publicMatchRepository.save(publicMatch);
    }
    // Eatzaz - Get public matches - need testing
    public PublicMatch getAllAvailableMatches(Integer playerId, Integer sportId, Integer fieldId){
        Player player=playerRepository.findPlayerById(playerId);
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        Sport sport=sportRepository.findSportById(sportId);
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        Field field=fieldRepository.findFieldById(fieldId);
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        if (field.getSport() != sport)
            throw new ApiException("Sports do not match");

//        List<PublicMatch> matches = publicMatchRepository.findPublicMatchByField(field);
//        TimeSlot timeSlot = timeSlotRepository.findTimeSlotByPublicMatchIsNotNull();
//        PublicMatch match = timeSlot.getPublicMatch();
//            if (match == null) {
//                throw new ApiException("Public Match Not Found");
//            }
//       return match;
        return null;
    }
    // Eatzaz - Get teams for public match
    public PublicMatchDTO getTeamsForPublicMatch(Integer PlayerId,Integer publicMatchId) {
        Player player=playerRepository.findPlayerById(PlayerId);
        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
        Field field=fieldRepository.findFieldById(match.getField().getId());
        if(player==null){
            throw new ApiException("player match not found");
        }
        if(field==null){
            throw new ApiException("Field match not found");
        }
        if(match==null){
        throw new ApiException("Public match not found");
        }
        Team team = match.getTeam();
        Team_DTO teamDto = new Team_DTO(null, team.getPublic_match().getName(), team.getPublic_match().getStatus(), team.getPlayersCount() , null);

        return new PublicMatchDTO(teamDto);
    }

    // Eatzaz - Choose a team - need testing
    public void PublicTeamSelection(Integer playerId, Integer sportId,Integer fieldId, String teamName) {
        Player player=playerRepository.findPlayerById(playerId);
        Sport sport=sportRepository.findSportById(sportId);
        Field field=fieldRepository.findFieldById(fieldId);
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(player.getPublic_match().getId());
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
            throw new ApiException("public Match Not Found");
        }
        Team team = teamRepository.findTeamAByName(teamName);
        if(team != null && team.getPlayersCount()< team.getMax_players_count()){
            publicMatch.getPlayers().add(player);
            team.setPlayersCount(team.getPlayersCount()+1);
            publicMatch.setTeam(team);
        }

    }
    // Eatzaz - Show player selections - need testing
    public PlayerSelectionDTO getPlayerMatchSelection(Integer playerId,Integer publicMatchId) {
        Player player = playerRepository.findPlayerById(playerId);
        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (player == null) {
            throw new ApiException("player Not Found");
        }
        if (publicMatch == null) {
            throw new ApiException("public Match Not Found");
        }
        String selectedTeamName = null;
        if (publicMatch.getTeam() != null && publicMatch.getPlayers().contains(player))
            selectedTeamName = publicMatch.getTeam().getName();
        List<TimeSlot> timeSlots = publicMatch.getTime_slots();
        if (timeSlots.isEmpty()) {
            throw new ApiException("No time slots found for this match");
        }

        TimeSlot slot = timeSlots.get(0); // أخذ أول وقت فقط

        return new PlayerSelectionDTO(
                publicMatch.getField().getName(),
                publicMatch.getField().getAddress(),
                slot.getPrice(),
                selectedTeamName,
                slot.getDate(),
                slot.getStart_time()
        );
    }


}
