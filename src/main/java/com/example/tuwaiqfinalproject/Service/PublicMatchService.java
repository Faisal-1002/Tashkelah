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

import java.util.ArrayList;
import java.time.LocalDate;
import java.util.ArrayList;
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
    private final PrivateMatchRepository privateMatchRepository;
    private final BookingRepository bookingRepository;

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
public void addPublicMatch(Integer organizerId, PublicMatch match, Integer fieldId, List<Integer> timeSlotIds) {
    Organizer organizer = organizerRepository.findOrganizerById(organizerId);
    if (organizer == null) {
        throw new ApiException("Organizer not found");
    }

    Field field = fieldRepository.findFieldById(fieldId);
    if (field == null) {
        throw new ApiException("Field not found");
    }

    List<TimeSlot> timeSlots = timeSlotRepository.findAllById(timeSlotIds);
    if (timeSlots.isEmpty()) {
        throw new ApiException("TimeSlot not found");
    }

    match.setStatus("OPEN");
    match.setOrganizer(organizer);
    match.setField(field);

    publicMatchRepository.save(match);

    for (TimeSlot slot : timeSlots) {
        slot.setStatus("BOOKED");
        slot.setPublic_match(match);
    }

    timeSlotRepository.saveAll(timeSlots);

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

    //Taha --------- (1)
    public List<?> showFieldMatches(Integer fieldId, Integer userId) {

        Organizer organizer = organizerRepository.findById(userId)
                .orElseThrow(() -> new ApiException("Organizer not found"));

        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));

        List<PublicMatch> publicMatches= publicMatchRepository.findPublicMatchesByField(field);
        List<PrivateMatch> privateMatches =privateMatchRepository.findPrivateMatchByField(field);

        if (!field.getOrganizer().getId().equals(organizer.getId())) {
            throw new ApiException("You are not authorized to view matches for this field");
        }


        List<Object> allMatches = new ArrayList<>();
        allMatches.addAll(publicMatches);
        allMatches.addAll(privateMatches);

        return allMatches;
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
        PublicMatch publicMatch = publicMatchRepository.findFirstByFieldAndStatusAndField_Sport(field, "OPEN",sport);
        if (publicMatch == null || !publicMatch.getField().getSport().getId().equals(sport.getId())) {
            throw new ApiException("No open public match found for this field and sport");
        }
        player.setPublic_match(publicMatch);
        publicMatch.getPlayers().add(player);
        publicMatchRepository.save(publicMatch);
    }


    //5- Eatzaz - Get public matches - tested
    public List<PublicMatch> getAllAvailablePublicMatches(Integer playerId, Integer sportId, Integer fieldId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) throw new ApiException("Player Not Found");

        Sport sport = sportRepository.findSportById(sportId);
        if (sport == null) throw new ApiException("Sport Not Found");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null) throw new ApiException("Field Not Found");

        if (!field.getSport().getId().equals(sport.getId()))
            throw new ApiException("Sport and field mismatch");

        // Return all public matches on this field (optionally check status/time if needed)
        return publicMatchRepository.findPublicMatchByField(field);
    }


    // 6. Eatzaz - Get teams for public match -tested
    public List<Team> getTeamsForPublicMatch(Integer PlayerId,Integer publicMatchId) {
        Player player = playerRepository.findPlayerById(PlayerId);
        if (player == null) {
            throw new ApiException("player match not found");
        }
        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (match == null) {
            throw new ApiException("Public match not found");
        }
        Field field = fieldRepository.findFieldById(match.getField().getId());
        if (field == null) {
            throw new ApiException("Field match not found");
        }
        return match.getTeam();
    }


    // 7- Eatzaz - Choose a team - tested
    public void PublicTeamSelection(Integer playerId, Integer sportId,Integer fieldId,Integer publicMatchId,Integer teamId) {
        Player player=playerRepository.findPlayerById(playerId);
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(publicMatchId);
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
        if(publicMatch==null){
            throw new ApiException("public Match Not Found");
        }
        if (!publicMatch.getPlayers().contains(player)) {
            throw new ApiException("Player is not Found of this public match");
        }

        Team team = teamRepository.findTeamById(teamId);
        if (team.getPublic_match() == null || !team.getPublic_match().getId().equals(publicMatch.getId())) {
            throw new ApiException("This team does not belong to the selected match");
        }
            team.setPlayersCount(team.getPlayersCount()+1);
            teamRepository.save(team);
        }


    // 8- Eatzaz - Show player selections - tested
    public PlayerSelectionDTO getPlayerMatchSelection(Integer playerId,Integer publicMatchId,Integer teamId) {
        Player player = playerRepository.findPlayerById(playerId);
        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (player == null) {
            throw new ApiException("player Not Found");
        }
        if (publicMatch == null) {
            throw new ApiException("public Match Not Found");
        }

Team team=teamRepository.findTeamById(teamId);
        String selectedTeamName = null;
        if (publicMatch.getTeam() != null && publicMatch.getPlayers().contains(player)) {
            selectedTeamName = team.getName();
        }

        List<TimeSlot> timeSlots = publicMatch.getTime_slots();
        if (timeSlots.isEmpty()) {
            throw new ApiException("No time slots found for this match");
        }
        return new PlayerSelectionDTO(
                publicMatch.getField().getName(),
                publicMatch.getField().getAddress(),
                selectedTeamName,
                publicMatch.getTime_slots()
        );
    }
// 11. Eatzaz - Notification that the payment process has been completed -teted
    public String Notifications(Integer playerId,Integer bookingId){
        Player player=playerRepository.findPlayerById(playerId);
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        Booking booking=bookingRepository.findBookingById(bookingId);
        if(booking==null){
            throw new ApiException("Booking Not Found");
        }
        if(! booking.getPublic_match().equals(player.getPublic_match()) && booking.getIs_paid().equals(true)){
            throw new ApiException("valid");
        }
        return changeStatusAfterCompleted(booking.getPublic_match().getId(),booking.getId());

    }
//12 . Eatzaz - Change the match status after the number is complete
public String changeStatusAfterCompleted(Integer publicMatchId, Integer bookingId) {
    PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
    if (publicMatch == null) {
        throw new ApiException("Public Match Not Found");
    }

    Booking booking = bookingRepository.findBookingById(bookingId);
    if (booking == null) {
        throw new ApiException("Booking Not Found");
    }

    List<Team> teams = publicMatch.getTeam();
    int numberPlayer = 0;
    for (Team team : teams) {
        numberPlayer += team.getPlayersCount();
    }


    if (numberPlayer == publicMatch.getField().getCapacity()) {
        publicMatch.setStatus("FULL");
        publicMatchRepository.save(publicMatch);
        return "Match status updated to FULL";
    }
    return "Booking successful, waiting for more players";
}

}
