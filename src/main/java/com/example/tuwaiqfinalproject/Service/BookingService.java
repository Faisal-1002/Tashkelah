package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final PlayerRepository playerRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final PrivateMatchRepository privateMatchRepository;
    private final PublicMatchRepository publicMatchRepository;

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    // 51. Faisal - Get booking by id - Tested
    public Booking getBookingById(Integer bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking == null)
            throw new ApiException("Booking not found");
        return booking;
    }

    // 37. Faisal - Get all my bookings - Tested
    public List<Booking> getMyBookings(Integer userId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        List<Booking> result = new ArrayList<>();

        // 1. Private match bookings
        List<PrivateMatch> privateMatches = privateMatchRepository.findPrivateMatchByPlayer(player);
        for (PrivateMatch match : privateMatches) {
            if (match.getBooking() != null) {
                result.add(match.getBooking());
            }
        }

        // 2. Public match bookings
        List<Booking> publicBookings = bookingRepository.findBookingsByPlayerInPublicMatch(player);
        result.addAll(publicBookings);

        return result;
    }

    // Faisal - update booking for public or private match - Tested
    public void updateBooking(Integer userId, Integer bookingId, Booking updatedBooking) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        Booking existing = bookingRepository.findBookingById(bookingId);
        if (existing == null)
            throw new ApiException("Booking not found");

        boolean isOwner = false;

        // Check if booking is part of a public match the player joined
        if (player.getPublic_match() != null) {
            isOwner = player.getPublic_match().getBookings().stream()
                    .anyMatch(b -> b.getId().equals(bookingId));
        }

        // Check if booking is part of a private match the player owns
        if (!isOwner && player.getPrivate_matches() != null) {
            isOwner = player.getPrivate_matches().stream()
                    .anyMatch(pm -> pm.getBooking() != null && pm.getBooking().getId().equals(bookingId));
        }

        if (!isOwner)
            throw new ApiException("You do not have permission to update this booking");

        updatedBooking.setId(existing.getId());
        bookingRepository.save(updatedBooking);
    }

    public void deleteBooking(Integer bookingId) {
        Booking booking = bookingRepository.findBookingById(bookingId);
        if (booking == null)
            throw new ApiException("Booking not found");
        bookingRepository.delete(booking);
    }

    // 52. Faisal - Book private match - Tested
    public void bookPrivateMatch(Integer userId, Integer matchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PrivateMatch match = privateMatchRepository.findPrivateMatchById(matchId);
        if (match == null || !"TIME_RESERVED".equals(match.getStatus()))
            throw new ApiException("Match not found or not in TIME_RESERVED status");

        if (!match.getPlayer().getId().equals(player.getId()))
            throw new ApiException("Unauthorized access to this match");

        List<TimeSlot> slots = match.getTime_slots();
        if (slots == null || slots.isEmpty())
            throw new ApiException("No time slots assigned to this match");

        // Validate all slots are available
        for (TimeSlot slot : slots) {
            if (!"AVAILABLE".equalsIgnoreCase(slot.getStatus()))
                throw new ApiException("One or more slots are already taken");
        }

        // Ensure slots are back-to-back
        slots.sort(Comparator.comparing(TimeSlot::getStart_time));
        for (int i = 1; i < slots.size(); i++) {
            if (!slots.get(i - 1).getEnd_time().equals(slots.get(i).getStart_time())) {
                throw new ApiException("Time slots must be back-to-back");
            }
        }

        // Mark slots as pending
        for (TimeSlot slot : slots) {
            slot.setStatus("PENDING");
        }

        // Calculate total price
        double totalPrice = slots.stream().mapToDouble(TimeSlot::getPrice).sum();

        // Create booking
        Booking booking = new Booking();
        booking.setPlayer(player);
        booking.setPrivate_match(match);
        booking.setBooking_time(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setIs_paid(false);
        booking.setTotal_amount(totalPrice);

        // Save everything
        bookingRepository.save(booking);
        timeSlotRepository.saveAll(slots);

        match.setBooking(booking);
        match.setStatus("AWAITING_PAYMENT");
        privateMatchRepository.save(match);
    }

    // 36. Eatzaz - Public match booking - Final Version
    public void bookPublicMatch(Integer userId, Integer publicMatchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (match == null)
            throw new ApiException("Public match not found");

        if (player.getPublic_match() == null || !player.getPublic_match().getId().equals(match.getId()))
            throw new ApiException("You are not assigned to this public match");

        Booking existing = bookingRepository.findByPlayerAndPublicMatch(player, match);
        if (existing != null)
            throw new ApiException("You already have a booking for this match");

        List<TimeSlot> slots = match.getTime_slots();
        if (slots == null || slots.isEmpty())
            throw new ApiException("No time slots assigned to this match");

        for (TimeSlot slot : slots) {
            if (!"PENDING".equals(slot.getStatus()))
                throw new ApiException("One or more slots are already booked");
        }

        double totalPrice = slots.stream().mapToDouble(TimeSlot::getPrice).sum();

        // ✅ Calculate total max players from both teams
        int teamCapacity = match.getTeams().stream()
                .mapToInt(Team::getMax_players_count)
                .sum();

        if (teamCapacity == 0)
            throw new ApiException("Invalid team capacity for price division");

        double perPlayerPrice = totalPrice / (double) teamCapacity;

        Booking booking = new Booking();
        booking.setPlayer(player);
        booking.setPublic_match(match);
        booking.setBooking_time(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setIs_paid(false);
        booking.setTotal_amount(perPlayerPrice);

        bookingRepository.save(booking);
    }

    // 13. Eatzaz - Get player's public match bookings using JPQL - Tested
    public List<Booking> getMyBookingForPublicMatch(Integer playerId) {
        Player player = playerRepository.findPlayerById(playerId);
        if (player == null) {
            throw new ApiException("Player not found");
        }
        List<Booking> myBookings = bookingRepository.findPublicMatchBookingsByPlayer(player);
        if (myBookings == null || myBookings.isEmpty()) {
            throw new ApiException("No public match bookings found for this player");
        }
        return myBookings;
    }

}

