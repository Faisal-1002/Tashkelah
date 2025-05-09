package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PublicMatchDTO;
import com.example.tuwaiqfinalproject.DTO.TeamA_DTO;
import com.example.tuwaiqfinalproject.DTO.TeamB_DTO;
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
    private final OrganizerRepository organizerRepository;
    private final PlayerRepository playerRepository;
    private final SportRepository sportRepository;
    private final FieldRepository fieldRepository;
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

    //Taha -
    public List<PublicMatch> showFieldMatches(Integer fieldId, Integer userId) {

        Organizer organizer = organizerRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Organizer not found"));

        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));

        if (!field.getOrganizer().getId().equals(organizer.getId())) {
            throw new ApiException("You are not authorized to view matches for this field");
        }

        return publicMatchRepository.findByField_Id(fieldId);
    }



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
        PublicMatch publicMatch = publicMatchRepository
                .findBySportAndFieldAndStatus(sport, field, "Pending");
        publicMatch.setPlayer(player);
        publicMatchRepository.save(publicMatch);
    }
    // عرض المباريات مع الوقت
    public PublicMatch getAllAvailableMatches(Integer playerId, Integer sportId, Integer fieldId){
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

        TimeSlot timeSlot = timeSlotRepository.findTimeSlotByField(field);
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchByField(field);
            if (publicMatch == null) {
                throw new ApiException("Public Match Not Found");
            }
                       return publicMatch;
        }
    // عرض الفرق وعدد اللاعبين
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
        TeamA teamA = match.getTeamA();
        TeamB teamB = match.getTeamB();
        TeamA_DTO teamADto = new TeamA_DTO(null,teamA.getPublicMatch().getName(),teamA.getPublicMatch().getStatus(),teamA.getTeamName(),teamA.getPlayersCount());
        TeamB_DTO teamBDto = new TeamB_DTO(null,teamB.getPublicMatch().getName(),teamB.getPublicMatch().getStatus(),teamB.getTeamName(),teamB.getPlayersCount());

        return new PublicMatchDTO(teamADto,teamBDto);
    }
    // اختيار فريق
    public void PublicTeamSelection(Integer playerId,Integer sportId,Integer fieldId){
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
            throw new ApiException("public Match Not Found");
        }
        if(player.getPublicMatch().getTeamA().equals(publicMatch.getTeamA())){
            publicMatch.setPlayer(player);
            TeamA teamA = publicMatch.getTeamA();
            teamA.setPlayersCount(teamA.getPlayersCount()+1);
            publicMatch.setTeamA(teamA);
        }
        publicMatch.setPlayer(player);
        publicMatch.setTeamB(publicMatch.getTeamB());
        TeamB teamB=publicMatch.getTeamB();
        teamB.setPlayersCount(teamB.getPlayersCount()+1);
    }
//
//    public PlayerSelectionDTO getPlayerMatchSelection(Integer playerId,Integer publicMatchId) {
//        Player player = playerRepository.findPlayerById(playerId);
//        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
//        if (player == null) {
//            throw new ApiException("player Not Found");
//        }
//
//        if (publicMatch == null) {
//            throw new ApiException("public Match Not Found");
//        }
//        String selectedTeamName = null;
//        if (publicMatch.getTeamA() != null && publicMatch.getPlayer().getId().equals(playerId)) {
//            selectedTeamName = publicMatch.getTeamA().getTeamName();
//        } else if (publicMatch.getTeamB() != null && publicMatch.getPlayer().getId().equals(playerId)) {
//            selectedTeamName = publicMatch.getTeamB().getTeamName();
//        }
//        List<TimeSlot> timeSlots = publicMatch.getTimeSlots();
//        if (timeSlots.isEmpty()) {
//            throw new ApiException("No time slots found for this match");
//        }
//
//        TimeSlot slot = timeSlots.get(0); // أخذ أول وقت فقط
//
//        return new PlayerSelectionDTO(
//                publicMatch.getField().getName(),
//                publicMatch.getField().getLocation(),
//                slot.getPrice(),
//                selectedTeamName,
//                slot.getDate(),
//                slot.getStartTime()
//        );
//    }


    }
