package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PlayerSelectionDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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
    private final TeamService teamService;
    private final WhatsAppService whatsAppService;

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

    // 63. Faisal - Get my public matches - Tested
    public List<PublicMatch> getMyPublicMatches(Integer playerId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Player not found");
        }

        List<Booking> bookings = bookingRepository.findBookingsByPlayerInPublicMatch(player);
        if (bookings.isEmpty()) {
            throw new ApiException("No public match bookings found");
        }

        return bookings.stream()
                .map(Booking::getPublic_match)
                .distinct()
                .collect(Collectors.toList());
    }

    public void updatePublicMatch(Integer userId, Integer matchId, PublicMatch updatedMatch) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        PublicMatch existing = publicMatchRepository.findPublicMatchById(matchId);
        if (existing == null)
            throw new ApiException("Public match not found");

        // Ensure the organizer is the creator of the match
        if (!existing.getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("You are not allowed to update this match");

        // Apply allowed updates
        existing.setStatus(updatedMatch.getStatus());
        existing.setTime_slots(updatedMatch.getTime_slots());
        existing.setField(updatedMatch.getField());
        existing.setTeams(updatedMatch.getTeams());

        publicMatchRepository.save(existing);
    }

    public void deletePublicMatch(Integer userId, Integer id) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        PublicMatch match = publicMatchRepository.findPublicMatchById(id);
        if (match == null)
            throw new ApiException("Public match not found");

        if (!match.getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("You are not allowed to delete this match");

        if (match.getPlayers() != null && !match.getPlayers().isEmpty())
            throw new ApiException("Cannot delete match with joined players");

        publicMatchRepository.delete(match);
    }

    // 20.Taha - Show public + private matches for a given filed - Tested
    public List<Object> showFieldMatches(Integer fieldId, Integer userId) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null)
            throw new ApiException("Field not found");

        if (!field.getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("You are not authorized to view matches for this field");

        List<PublicMatch> publicMatches = publicMatchRepository.findPublicMatchesByField(field);
        List<PrivateMatch> privateMatches = privateMatchRepository.findPrivateMatchByField(field);

        List<Object> allMatches = new ArrayList<>();
        allMatches.addAll(publicMatches);
        allMatches.addAll(privateMatches);

        return allMatches;
    }

    // 28. Eatzaz - Play with a public match - Tested
    public void playWithPublicMatch(Integer userId, Integer publicMatchId, Integer teamId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (publicMatch == null)
            throw new ApiException("Public match not found");

        Field field = publicMatch.getField();
        if (field == null)
            throw new ApiException("Field not found");

        Sport sport = field.getSport();
        if (sport == null)
            throw new ApiException("Sport not found");

        Team selectedTeam = publicMatch.getTeams().stream()
                .filter(t -> t.getId().equals(teamId))
                .findFirst()
                .orElseThrow(() -> new ApiException("Team not found in this match"));

        if (publicMatch.getPlayers().contains(player))
            throw new ApiException("Player already joined a team in this match");

        if (!selectedTeam.getPublic_match().getId().equals(publicMatch.getId()))
            throw new ApiException("This team does not belong to the selected match");

        selectedTeam.setPlayersCount(selectedTeam.getPlayersCount() + 1);
        publicMatch.getPlayers().add(player);
        player.setPublic_match(publicMatch);

        teamRepository.save(selectedTeam);
        publicMatchRepository.save(publicMatch);
        playerRepository.save(player);
    }

    // 29. Eatzaz - Get public matches - Tested
    public List<PublicMatch> getAllAvailablePublicMatches(Integer playerId, Integer sportId, Integer fieldId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null)
            throw new ApiException("Player not found");

        Sport sport = sportRepository.findSportById(sportId);
        if (sport == null)
            throw new ApiException("Sport not found");

        Field field = fieldRepository.findFieldById(fieldId);
        if (field == null)
            throw new ApiException("Field not found");

        if (!field.getSport().getId().equals(sport.getId()))
            throw new ApiException("Field does not belong to the selected sport");

        List<PublicMatch> matches = publicMatchRepository.findPublicMatchesByField(field);
        if (matches.isEmpty())
            throw new ApiException("No public matches found for this field");

        return matches;
    }

    // 30. Eatzaz - Get teams for public match - Tested
    public List<Team> getTeamsForPublicMatch(Integer playerId, Integer publicMatchId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Player not found");
        }

        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (match == null) {
            throw new ApiException("Public match not found");
        }

        Field field = match.getField();
        if (field == null) {
            throw new ApiException("Field not assigned to this match");
        }

        return match.getTeams();
    }

    // 32. Eatzaz - Show player selections - Tested
    public PlayerSelectionDTO getPlayerMatchSelection(Integer playerId, Integer publicMatchId, Integer teamId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Player not found");
        }
        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (publicMatch == null) {
            throw new ApiException("Public match not found");
        }
        Team team = teamRepository.findTeamById(teamId);
        if (team == null || !publicMatch.getTeams().contains(team)) {
            throw new ApiException("Team not found or does not belong to this match");
        }
        if (!publicMatch.getPlayers().contains(player)) {
            throw new ApiException("Player is not part of this match");
        }
        List<TimeSlot> timeSlots = publicMatch.getTime_slots();
        if (timeSlots == null || timeSlots.isEmpty()) {
            throw new ApiException("No time slots found for this match");
        }
        return new PlayerSelectionDTO(
                publicMatch.getField().getName(),
                publicMatch.getField().getAddress(),
                team.getName(),
                timeSlots
        );
    }

    // 33. Eatzaz - Notification that the payment process has been completed - Tested
    public void notifications(Integer playerId, Integer bookingId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Player not found");
        }

        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking == null) {
            throw new ApiException("Booking not found");
        }

        PublicMatch match = booking.getPublic_match();
        if (!match.equals(player.getPublic_match()) || !Boolean.TRUE.equals(booking.getIs_paid())) {
            throw new ApiException("Invalid booking or payment not completed");
        }
        // 1️⃣ Send Email
        String to = player.getUser().getEmail();
        String subject = "Booking Confirmed!";
        String body = "Hello " + player.getUser().getName() + ",\n\n" +
                "Your booking has been confirmed for the public match at:\n" +
                "Field: " + match.getField().getName() + "\n" +
                "Location: " + match.getField().getAddress() + "\n\n" +
                "Thank you for playing with us!\n\n" +
                "- Tashkelah Team";
        emailsService.sendEmail(to, subject, body);

        // 2️⃣ Trigger status update if match is full
        Integer organizerId = match.getField().getOrganizer().getId();
        changeStatusAfterCompleted(organizerId, match.getId());
    }

    // 34. Eatzaz - Change the match status after the number is complete - Tested
    public void changeStatusAfterCompleted(Integer organizerId, Integer publicMatchId) {
        Organizer organizer = organizerRepository.findOrganizerById(organizerId);
        if (organizer == null) {
            throw new ApiException("Organizer not found");
        }

        PublicMatch publicMatch = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (publicMatch == null) {
            throw new ApiException("Public Match Not Found");
        }

        if (!publicMatch.getField().getOrganizer().getId().equals(organizer.getId())) {
            throw new ApiException("Unauthorized: This match does not belong to your fields");
        }

        List<Team> teams = publicMatch.getTeams();
        int numberPlayer = teams.stream().mapToInt(Team::getPlayersCount).sum();
        int fieldCapacity = publicMatch.getField().getCapacity();

        if (numberPlayer >= fieldCapacity) {
            publicMatch.setStatus("FULL");
            publicMatchRepository.save(publicMatch);
            String body = "Dear " + organizer.getUser().getName() + ",\n\n"
                    + "The public match at your field '" + publicMatch.getField().getName() + "' is now FULL.\n"
                    + "Consider creating another match to accommodate more players.\n\nRegards.";

            whatsAppService.sendMessage(organizer.getUser().getPhone(), body);
        }
    }

    // 19. Taha - Create public match - Tested
    public void createPublicMatch(Integer userId, Integer fieldId, List<Integer> slotIds) {
        Organizer organizer = organizerRepository.findOrganizerById(userId);
        if (organizer == null)
            throw new ApiException("Organizer not found");

        Field field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new ApiException("Field not found"));

        if (!field.getOrganizer().getId().equals(organizer.getId()))
            throw new ApiException("Unauthorized access to this field");

        List<TimeSlot> slots = timeSlotRepository.findAllById(slotIds);
        if (slots.isEmpty())
            throw new ApiException("No time slots found for the given IDs");

        // Validate slots
        for (TimeSlot slot : slots) {
            if (!slot.getField().getId().equals(fieldId))
                throw new ApiException("Slot does not belong to the selected field");
            if (!slot.getStatus().equals("AVAILABLE"))
                throw new ApiException("One or more slots are not available");
        }

        // Sort and ensure back-to-back continuity
        slots.sort(Comparator.comparing(TimeSlot::getStart_time));
        for (int i = 1; i < slots.size(); i++) {
            LocalTime prevEnd = slots.get(i - 1).getEnd_time();
            LocalTime currStart = slots.get(i).getStart_time();
            if (!prevEnd.equals(currStart))
                throw new ApiException("Time slots must be continuous (back-to-back)");
        }

        // Create and save the public match
        PublicMatch match = new PublicMatch();
        match.setStatus("OPEN");
        match.setField(field);
        publicMatchRepository.save(match);

        // Assign slots to the match and mark them as PENDING
        for (TimeSlot slot : slots) {
            slot.setStatus("PENDING");
            slot.setPublic_match(match);
        }
        timeSlotRepository.saveAll(slots);

        // Create empty teams for players to join
        //teamService.addTeamsForPublicMatch(userId, match.getId());
    }
}
