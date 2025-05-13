package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.DTO.PlayerSelectionDTO;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PublicMatchServiceTest {

    @Mock
    private PublicMatchRepository publicMatchRepository;

    @Mock
    private PlayerRepository playerRepository;

    @Mock
    private SportRepository sportRepository;

    @Mock
    private FieldRepository fieldRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private OrganizerRepository organizerRepository;

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private PrivateMatchRepository privateMatchRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private EmailsService emailsService;

    @Mock
    private TeamService teamService;

    @Mock
    private WhatsAppService whatsAppService;

    @InjectMocks
    private PublicMatchService publicMatchService;

    private Player player;
    private Organizer organizer;
    private Field field;
    private Sport sport;
    private PublicMatch publicMatch;
    private Team team;
    private TimeSlot timeSlot;
    private Booking booking;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setId(1);
        user.setName("Test User");
        user.setEmail("test@example.com");
        user.setPhone("1234567890");

        player = new Player();
        player.setId(1);
        player.setUser(user);

        organizer = new Organizer();
        organizer.setId(1);
        organizer.setUser(user);

        sport = new Sport();
        sport.setId(1);
        sport.setName("Football");

        field = new Field();
        field.setId(1);
        field.setName("Test Field");
        field.setAddress("Test Address");
        field.setSport(sport);
        field.setOrganizer(organizer);
        field.setCapacity(10);

        publicMatch = new PublicMatch();
        publicMatch.setId(1);
        publicMatch.setStatus("OPEN");
        publicMatch.setField(field);
        publicMatch.setPlayers(new ArrayList<>());

        team = new Team();
        team.setId(1);
        team.setName("Team A");
        team.setPublic_match(publicMatch);
        team.setPlayersCount(0);

        timeSlot = new TimeSlot();
        timeSlot.setId(1);
        timeSlot.setStart_time(LocalTime.of(10, 0));
        timeSlot.setEnd_time(LocalTime.of(11, 0));
        timeSlot.setStatus("AVAILABLE");
        timeSlot.setField(field);

        booking = new Booking();
        booking.setId(1);
        booking.setPlayer(player);
        booking.setPublic_match(publicMatch);
        booking.setIs_paid(true);
    }

    @Test
    public void getAllPublicMatches_Success() {
        when(publicMatchRepository.findAll()).thenReturn(List.of(publicMatch));

        List<PublicMatch> result = publicMatchService.getAllPublicMatches();

        assertEquals(1, result.size());
        assertEquals(publicMatch, result.get(0));
    }

    @Test
    public void getPublicMatchById_Success() {
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        PublicMatch result = publicMatchService.getPublicMatchById(1);

        assertEquals(publicMatch, result);
    }

    @Test
    public void getPublicMatchById_NotFound() {
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(null);

        assertThrows(ApiException.class, () -> publicMatchService.getPublicMatchById(1));
    }

    @Test
    public void getMyPublicMatches_Success() {
        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(bookingRepository.findBookingsByPlayerInPublicMatch(player)).thenReturn(List.of(booking));

        List<PublicMatch> result = publicMatchService.getMyPublicMatches(1);

        assertEquals(1, result.size());
        assertEquals(publicMatch, result.get(0));
    }

    @Test
    public void getMyPublicMatches_PlayerNotFound() {
        when(playerRepository.findPlayerById(1)).thenReturn(null);

        assertThrows(ApiException.class, () -> publicMatchService.getMyPublicMatches(1));
    }

    @Test
    public void updatePublicMatch_Success() {
        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        PublicMatch updated = new PublicMatch();
        updated.setStatus("FULL");
        updated.setField(field);
        updated.setTime_slots(List.of(timeSlot));

        publicMatchService.updatePublicMatch(1, 1, updated);

        verify(publicMatchRepository).save(publicMatch);
        assertEquals("FULL", publicMatch.getStatus());
    }

    @Test
    public void updatePublicMatch_Unauthorized() {
        Organizer otherOrganizer = new Organizer();
        otherOrganizer.setId(2);

        when(organizerRepository.findOrganizerById(1)).thenReturn(otherOrganizer);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        assertThrows(ApiException.class, () -> publicMatchService.updatePublicMatch(1, 1, new PublicMatch()));
    }

    @Test
    public void deletePublicMatch_Success() {
        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        publicMatchService.deletePublicMatch(1, 1);

        verify(publicMatchRepository).delete(publicMatch);
    }

    @Test
    public void deletePublicMatch_WithPlayers_ThrowsException() {
        publicMatch.getPlayers().add(player);

        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        assertThrows(ApiException.class, () -> publicMatchService.deletePublicMatch(1, 1));
    }

    @Test
    public void showFieldMatches_Success() {
        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(fieldRepository.findFieldById(1)).thenReturn(field);
        when(publicMatchRepository.findPublicMatchesByField(field)).thenReturn(List.of(publicMatch));
        when(privateMatchRepository.findPrivateMatchByField(field)).thenReturn(new ArrayList<>());

        List<Object> result = publicMatchService.showFieldMatches(1, 1);

        assertEquals(1, result.size());
        assertTrue(result.contains(publicMatch));
    }

    @Test
    public void playWithPublicMatch_Success() {
        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);
        when(teamRepository.findTeamById(1)).thenReturn(team);

        publicMatch.getTeams().add(team);

        publicMatchService.playWithPublicMatch(1, 1, 1);

        assertEquals(1, team.getPlayersCount());
        assertTrue(publicMatch.getPlayers().contains(player));
        verify(teamRepository).save(team);
        verify(publicMatchRepository).save(publicMatch);
        verify(playerRepository).save(player);
    }

    @Test
    public void playWithPublicMatch_AlreadyJoined() {
        publicMatch.getPlayers().add(player);

        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        assertThrows(ApiException.class, () -> publicMatchService.playWithPublicMatch(1, 1, 1));
    }

    @Test
    public void getAllAvailablePublicMatches_Success() {
        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(sportRepository.findSportById(1)).thenReturn(sport);
        when(fieldRepository.findFieldById(1)).thenReturn(field);
        when(publicMatchRepository.findPublicMatchesByField(field)).thenReturn(List.of(publicMatch));

        List<PublicMatch> result = publicMatchService.getAllAvailablePublicMatches(1, 1, 1);

        assertEquals(1, result.size());
        assertEquals(publicMatch, result.get(0));
    }

    @Test
    public void getTeamsForPublicMatch_Success() {
        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        publicMatch.getTeams().add(team);

        List<Team> result = publicMatchService.getTeamsForPublicMatch(1, 1);

        assertEquals(1, result.size());
        assertEquals(team, result.get(0));
    }

    @Test
    public void getPlayerMatchSelection_Success() {
        publicMatch.getTeams().add(team);
        publicMatch.getPlayers().add(player);
        publicMatch.getTime_slots().add(timeSlot);

        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);
        when(teamRepository.findTeamById(1)).thenReturn(team);

        PlayerSelectionDTO result = publicMatchService.getPlayerMatchSelection(1, 1, 1);

        assertEquals(field.getName(), result.getFieldName());
        assertEquals(field.getAddress(), result.getFieldAddress());
        assertEquals(1, result.getTimeSlots().size());
    }

    @Test
    public void notifications_Success() {
        when(playerRepository.findPlayerById(1)).thenReturn(player);
        when(bookingRepository.findBookingById(1)).thenReturn(booking);

        publicMatchService.notifications(1, 1);

        verify(emailsService).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void changeStatusAfterCompleted_WhenFull() {
        team.setPlayersCount(10);
        publicMatch.getTeams().add(team);

        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(publicMatchRepository.findPublicMatchById(1)).thenReturn(publicMatch);

        publicMatchService.changeStatusAfterCompleted(1, 1);

        assertEquals("FULL", publicMatch.getStatus());
        verify(whatsAppService).sendMessage(anyString(), anyString());
    }

    @Test
    public void createPublicMatch_Success() {
        TimeSlot slot2 = new TimeSlot();
        slot2.setId(2);
        slot2.setStart_time(LocalTime.of(11, 0));
        slot2.setEnd_time(LocalTime.of(12, 0));
        slot2.setStatus("AVAILABLE");
        slot2.setField(field);

        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
        when(timeSlotRepository.findAllById(List.of(1, 2))).thenReturn(List.of(timeSlot, slot2));

        publicMatchService.createPublicMatch(1, 1, List.of(1, 2));

        verify(publicMatchRepository).save(any(PublicMatch.class));
        verify(timeSlotRepository).saveAll(anyList());
        verify(teamService).addTeamsForPublicMatch(1, anyInt());
    }

    @Test
    public void createPublicMatch_NonContinuousSlots_ThrowsException() {
        TimeSlot slot2 = new TimeSlot();
        slot2.setId(2);
        slot2.setStart_time(LocalTime.of(12, 0)); // Not continuous with first slot
        slot2.setEnd_time(LocalTime.of(13, 0));
        slot2.setStatus("AVAILABLE");
        slot2.setField(field);

        when(organizerRepository.findOrganizerById(1)).thenReturn(organizer);
        when(fieldRepository.findById(1)).thenReturn(Optional.of(field));
        when(timeSlotRepository.findAllById(List.of(1, 2))).thenReturn(List.of(timeSlot, slot2));

        assertThrows(ApiException.class, () -> publicMatchService.createPublicMatch(1, 1, List.of(1, 2)));
    }
}