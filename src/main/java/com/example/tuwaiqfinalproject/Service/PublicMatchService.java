package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PublicMatchDTO;
import com.example.tuwaiqfinalproject.DTO.Team_DTO;
import com.example.tuwaiqfinalproject.DTO.PlayerSelectionDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
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
    // add timeSlot

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
            throw new ApiException("TimeSlot(s) not found ");
        }

        for (TimeSlot slot : timeSlots) {
            if (!"AVAILABLE".equals(slot.getStatus())) {
                throw new ApiException("TimeSlot with ID " + slot.getId() + " is not available ");
            }
            slot.setStatus("BOOKED");
        }

        timeSlotRepository.saveAll(timeSlots);

        match.setStatus("OPEN");
        match.setOrganizer(organizer);
        match.setField(field);
        match.setTime_slots(timeSlots);
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


    // 8- Eatzaz - Show player selections - need testing
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
// 11. Eatzaz - Notification that the payment process has been completed
    public void Notifications(Integer playerId,Integer bookingId){
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
    }
//12 . Eatzaz - Change the match status after the number is complete
public void changeStatusAfterCompleted(Integer publicMatchId, Integer bookingId) {
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


    if (numberPlayer != publicMatch.getField().getCapacity()) {
        throw new ApiException("Number of players does not match the field capacity");
    }

    publicMatch.setStatus("FULL");

    publicMatchRepository.save(publicMatch);
}


    // Taha ---------------------------  createMatchFromTimeSlots
    public void createMatchFromTimeSlots(Integer fieldId, List<Integer> slotIds) {
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));

        List<TimeSlot> slots = timeSlotRepository.findAllById(slotIds);

        if (slots.isEmpty())
            throw new ApiException("No slots found for the given IDs");

        // Validate all slots belong to same field and same day and are available
        LocalDate date = slots.get(0).getDate();
        for (TimeSlot slot : slots) {
            if (!slot.getField().getId().equals(fieldId))
                throw new ApiException("All slots must belong to the same field");
            if (!slot.getDate().equals(date))
                throw new ApiException("All slots must be on the same day");
            if (!slot.getStatus().equals("AVAILABLE"))
                throw new ApiException("One or more slots are not available");
        }

        // Ensure the slots are continuous
        slots.sort(Comparator.comparing(TimeSlot::getStart_time));
        for (int i = 0; i < slots.size() - 1; i++) {
            if (!slots.get(i).getEnd_time().equals(slots.get(i + 1).getStart_time()))
                throw new ApiException("Selected slots must be continuous");
        }

        // Create the match
        PublicMatch match = new PublicMatch();
        match.setStatus("PENDING");
        match.setField(field);
        publicMatchRepository.save(match);

        // Link slots to match
        for (TimeSlot slot : slots) {
            slot.setStatus("BOOKED");
            slot.setPublic_match(match);
        }
        timeSlotRepository.saveAll(slots);

        // Create two teams and assign to match
        Team teamA = new Team();
        teamA.setName("Team A");
        teamA.setPlayersCount(0);
        teamA.setMax_players_count(field.getCapacity() / 2);
        teamA.setPublic_match(match);

        Team teamB = new Team();
        teamB.setName("Team B");
        teamB.setPlayersCount(0);
        teamB.setMax_players_count(field.getCapacity() / 2);
        teamB.setPublic_match(match);

        teamRepository.save(teamA);
        teamRepository.save(teamB);
    }

}
