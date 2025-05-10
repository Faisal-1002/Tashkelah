package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PublicMatchDTO;
import com.example.tuwaiqfinalproject.DTO.Team_DTO;
import com.example.tuwaiqfinalproject.DTO.PlayerSelectionDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import jakarta.persistence.criteria.CriteriaBuilder;
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
    private final OrganizerRepository organizerRepository;
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
// 3 - Eatzaz - add Public match with Field - tested
    public void addPublicMatch(Integer organizerId,PublicMatch match,Integer fieldId) {
        Organizer organizer=organizerRepository.findOrganizerById(organizerId);
        if(organizer==null){
            throw new ApiException("organizer not found");
        }
        Field field=fieldRepository.findFieldById(fieldId);
        if(field==null){
            throw new ApiException("organizer not found");
        }
        match.setStatus("OPEN");
        match.setOrganizer(organizer);
        match.setField(field);
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

    //4. Eatzaz- Play with a public match -tested
    public void PlayWithPublicMatch(Integer sportId,Integer fieldId,Integer playerId){
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
        PublicMatch publicMatch = publicMatchRepository.findPublicMatchByFieldAndStatus(field, "OPEN");
        if (publicMatch == null || !publicMatch.getField().getSport().getId().equals(sport.getId())) {
            throw new ApiException("No open public match found for this field and sport");
        }
        publicMatch.getPlayers().add(player);
        publicMatchRepository.save(publicMatch);
    }
//    5- Eatzaz - Get public matches - tested
    public List<PublicMatch> getAllAvailableMatches(Integer playerId, Integer sportId, Integer fieldId){
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

        List<PublicMatch> matches = publicMatchRepository.findPublicMatchByField(field);
        TimeSlot timeSlot = timeSlotRepository.findTimeSlotWherePublicMatchIsNotNull();
        PublicMatch match = timeSlot.getPublic_match();
            if (match == null) {
                throw new ApiException("Public Match Not Found");
            }
       return matches;
    }

    // Eatzaz - Get teams for public match
//    public PublicMatchDTO getTeamsForPublicMatch(Integer PlayerId,Integer publicMatchId) {
//        Player player=playerRepository.findPlayerById(PlayerId);
//        if(player==null){
//            throw new ApiException("player match not found");
//        }
//        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
//        if(match==null){
//        throw new ApiException("Public match not found");
//        }
//        Field field=fieldRepository.findFieldById(match.getField().getId());
//        if(field==null){
//            throw new ApiException("Field match not found");
//        }
//        Team team = match.getTeam();
//        if (team == null) {
//            throw new ApiException("No team assigned to this match");
//        }
//        if (team.getPublicMatch() == null) {
//            throw new ApiException("Public match data missing in team");
//        }
//        Team_DTO teamADto = new Team_DTO(null, team.getPublicMatch().getName(), team.getPublicMatch().getStatus(), team.getName(), team.getPlayersCount());
//        return new PublicMatchDTO(teamADto);
//    }
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
