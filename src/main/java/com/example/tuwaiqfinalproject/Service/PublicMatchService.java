package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PlayerSelectionDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.LocalTime;
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
    private final EmailsService emailsService;
    private final AuthRepository authRepository;
    private final TeamService teamService;

    public List<PublicMatch> getAllPublicMatches() {
        return publicMatchRepository.findAll();
    }

    // 18. Faisal - Get public match by id - Tested
    public PublicMatch getPublicMatchById(Integer id) {
        PublicMatch match = publicMatchRepository.findPublicMatchById(id);
        if (match == null)
            throw new ApiException("Public match not found");
        return match;
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

    // 20.Taha - Show public + private matches for a given filed - Tested
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
  
    // update Config
    // 28. Eatzaz - Play with a public match - Tested
    public void PlayWithPublicMatch(Integer publicMatchId,Integer teamId,Integer playerId){
        Player player=playerRepository.findPlayerById(playerId);
        if(player==null){
            throw new ApiException("Player Not Found");
        }

        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(publicMatchId);
        Field field=publicMatch.getField();
        if(field==null){
            throw new ApiException("Field Not Found");
        }
        Sport sport=field.getSport();
        if(sport==null){
            throw new ApiException("Sport Not Found");
        }
        Team selectedTeam = publicMatch.getTeam().stream()
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new ApiException("Team Not Found in this match"));

        boolean alreadyInTeam=publicMatch.getTeam().stream()
                        .anyMatch(team -> publicMatch.getPlayers().contains(player));
        if(alreadyInTeam){
            throw new ApiException("Player already joined a team in this match");
        }

        Team team = teamRepository.findTeamById(teamId);
        if (team.getPublic_match() == null || !team.getPublic_match().getId().equals(publicMatch.getId())) {
            throw new ApiException("This team does not belong to the selected match");
        }
        team.setPlayersCount(team.getPlayersCount()+1);
        teamRepository.save(team);
        selectedTeam.getPublic_match().getPlayers().add(player);
        player.setPublic_match(publicMatch);
        publicMatchRepository.save(publicMatch);
        playerRepository.save(player);

    }

    //update config
    // 29. Eatzaz - Get public matches - Tested
    public List<?> getAllAvailablePublicMatches(Integer playerId, Integer publicMatchId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) 
            throw new ApiException("Player Not Found");
        PublicMatch publicMatch=publicMatchRepository.findPublicMatchById(publicMatchId);
        if(publicMatch==null)
            throw new ApiException("Public Match Not Found");
      
        Field field = fieldRepository.findFieldById(publicMatch.getField().getId());
        if (field == null) throw new ApiException("Field Not Found");

        Sport sport = sportRepository.findSportById(field.getSport().getId());
        if (sport == null) throw new ApiException("Sport Not Found");

        if (!field.getSport().getId().equals(sport.getId()))
            throw new ApiException("Sport and field mismatch");

        return publicMatch.getTime_slots();
    }

    // 30. Eatzaz - Get teams for public match - Tested
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
        return match.getTeams();
    }

    // 32. Eatzaz - Show player selections - Need testing
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
        if (publicMatch.getTeams() != null && publicMatch.getPlayers().contains(player)) {
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

    // 33. Eatzaz - Notification that the payment process has been completed - Tested
    public void Notifications(Integer playerId,Integer bookingId){
        Player player=playerRepository.findPlayerById(playerId);
        if(player==null){
            throw new ApiException("Player Not Found");
        }
        Booking booking=bookingRepository.findBookingById(bookingId);
        if(booking==null){
            throw new ApiException("Booking Not Found");
        }
        if(! booking.getPublic_match().equals(player.getPublic_match()) &&  booking.getIs_paid().equals(false)){
            throw new ApiException("valid");
        }

    }

    // 34 . Eatzaz - Change the match status after the number is complete
    public void changeStatusAfterCompleted(Integer publicMatchId) {
        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (publicMatch == null) {
            throw new ApiException("Public Match Not Found");
        }

        List<Team> teams = publicMatch.getTeam();
        int numberPlayer = 0;
        for (Team team : teams) {
            numberPlayer += team.getPlayersCount();
        }
        if (numberPlayer == publicMatch.getField().getCapacity()) {
            publicMatch.setStatus("FULL");
            publicMatchRepository.save(publicMatch);
            emailsService.sendEmail("faisal.a.m.2012@gmail.com","Match status updated to FULL","Match status updated to FULL");
            throw new ApiException( "Match status updated to FULL");
        }
    }

    // 19. Taha - Create public match - Tested
    public void createMatchFromTimeSlots(Integer userId, Integer fieldId, List<Integer> slotIds) {
        Organizer organizer=organizerRepository.findOrganizerById(userId);
        if(organizer==null)
            throw new ApiException("Organizer Not Found");
        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));

        Organizer organizer = organizerRepository.findOrganizerById(userId);

        if (organizer== null){
            throw new ApiException("Organizer not found");
        }

        if (!field.getOrganizer().getId().equals(organizer.getId())){
            throw new ApiException("Unauthorized access");
        }


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
        teamService.addTeamsForPublicMatch(userId, match.getId());
    }

}
