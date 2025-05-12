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

    public void updateBooking(Integer bookingId, Booking updatedBooking) {
        Booking existing = bookingRepository.findBookingById(bookingId);
        if (existing == null)
            throw new ApiException("Booking not found");

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
        if (player == null) throw new ApiException("Player not found");

        PrivateMatch match = privateMatchRepository.findPrivateMatchById(matchId);
        if (match == null || !match.getStatus().equals("TIME_RESERVED"))
            throw new ApiException("Match not found or not in TIME_RESERVED status");

        if (!match.getPlayer().getId().equals(player.getId()))
            throw new ApiException("Unauthorized access to this match");

        List<TimeSlot> slots = match.getTime_slots();
        if (slots == null || slots.isEmpty())
            throw new ApiException("No time slots assigned to this match");

        for (TimeSlot slot : slots) {
            if (!slot.getStatus().equalsIgnoreCase("AVAILABLE"))
                throw new ApiException("One or more slots have already been booked");
            slot.setStatus("BOOKED");
        }

        Double totalPrice = slots.stream().mapToDouble(TimeSlot::getPrice).sum();

        Booking booking = new Booking();
        booking.setPrivate_match(match);
        booking.setBooking_time(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setIs_paid(false);
        booking.setTotal_amount(totalPrice);

        bookingRepository.save(booking);
        timeSlotRepository.saveAll(slots);

        match.setBooking(booking);
        match.setStatus("AWAITING_PAYMENT");
        privateMatchRepository.save(match);
    }

    // 36. Eatzaz - Public match booking - Need testing
    public void bookPublicMatch(Integer userId, List<Integer> slotIds) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");
        PublicMatch match = player.getPublic_match();
        if (match == null ) {
            throw new ApiException("Match not found ");
        }

        Field field = match.getField();
        if (field == null)
            throw new ApiException("No field assigned to this match");

        List<TimeSlot> slots = timeSlotRepository.findAllById(slotIds);
        if (slots.size() != slotIds.size())
            throw new ApiException("One or more time slots are invalid");

        for (TimeSlot slot : slots) {
            if (!slot.getField().getId().equals(field.getId()))
                throw new ApiException("TimeSlot does not belong to the assigned field");
            if (!slot.getStatus().equals("AVAILABLE") && !slot.getStatus().equals("PENDING"))
                throw new ApiException("One or more time slots are already booked");
        }

        // Check if time slots are back-to-back
        slots.sort(Comparator.comparing(TimeSlot::getStart_time));
        for (int i = 1; i < slots.size(); i++) {
            if (!slots.get(i - 1).getEnd_time().equals(slots.get(i).getStart_time())) {
                throw new ApiException("Time slots must be back-to-back");
            }
        }

        // Book the slots
        for (TimeSlot slot : slots) {
            slot.setStatus("BOOKED");
        }

        Double totalPrice = slots.stream().mapToDouble(TimeSlot::getPrice).sum();

        Booking booking = new Booking();
        match.getBookings().add(booking);
        booking.setPublic_match(match);
        booking.setBooking_time(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setIs_paid(false);
        booking.setTotal_amount(totalPrice);

        match.getBookings().add(booking);
        bookingRepository.save(booking);
        timeSlotRepository.saveAll(slots);
        match.setStatus("PENDING");
        publicMatchRepository.save(match);
    }

    //13. Eatzaz - get Player My Booking - tested
    public List<Booking> getMyBookingForPublicMatch(Integer playerId){
        Player player=playerRepository.findPlayerById(playerId);
        if(player==null)
            throw new ApiException("Player Not Found !");

        List<Booking>myBookings=bookingRepository.findBookingsByPlayerInPublicMatch(player);
        if (myBookings.isEmpty())
            throw new ApiException("You have no bookings in public matches.");

        return myBookings;
    }
}

