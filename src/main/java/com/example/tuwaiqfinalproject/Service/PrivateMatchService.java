package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.BookingRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import com.example.tuwaiqfinalproject.Repository.PrivateMatchRepository;
import com.example.tuwaiqfinalproject.Repository.TimeSlotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PrivateMatchService {

    private final PrivateMatchRepository privateMatchRepository;
    private final PlayerRepository playerRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BookingRepository bookingRepository;

    public List<PrivateMatch> getAllPrivateMatches() {
        return privateMatchRepository.findAll();
    }

    public PrivateMatch getPrivateMatchById(Integer id) {
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(id);
        if (match == null)
            throw new ApiException("Private match not found");
        return match;
    }

    public void updatePrivateMatch(Integer id, PrivateMatch updatedMatch) {
        PrivateMatch existing = privateMatchRepository.findPrivateMatchById(id);
        if (existing == null)
            throw new ApiException("Private match not found");

        updatedMatch.setId(existing.getId());
        privateMatchRepository.save(updatedMatch);
    }

    public void deletePrivateMatch(Integer id) {
        PrivateMatch match = privateMatchRepository.findPrivateMatchById(id);
        if (match == null)
            throw new ApiException("Private match not found");
        privateMatchRepository.delete(match);
    }

    // 23. Faisal - Create private match - Tested
    public void createPrivateMatch(Integer user_id, PrivateMatch privateMatch) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null)
            throw new ApiException("Player not found");
        privateMatch.setPlayer(player);
        privateMatch.setStatus("PENDING");
        privateMatchRepository.save(privateMatch);
    }

    // 26. Faisal - Book a private match - Tested
    public void bookPrivateMatch(Integer userId, List<Integer> slotIds) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null) throw new ApiException("Player not found");

        PrivateMatch match = player.getPrivateMatch();
        if (match == null || !match.getStatus().equals("SCHEDULED"))
            throw new ApiException("Match not found or not scheduled");

        Field field = match.getField();
        if (field == null)
            throw new ApiException("No field assigned to this match");

        List<TimeSlot> slots = timeSlotRepository.findAllById(slotIds);
        if (slots.size() != slotIds.size())
            throw new ApiException("One or more time slots are invalid");

        for (TimeSlot slot : slots) {
            if (!slot.getField().getId().equals(field.getId()))
                throw new ApiException("TimeSlot does not belong to the assigned field");
            if (!slot.getStatus().equals("AVAILABLE"))
                throw new ApiException("One or more time slots are already booked");
        }

        // Check if time slots are back-to-back
        slots.sort(Comparator.comparing(TimeSlot::getStartTime));
        for (int i = 1; i < slots.size(); i++) {
            if (!slots.get(i - 1).getEndTime().equals(slots.get(i).getStartTime())) {
                throw new ApiException("Time slots must be back-to-back");
            }
        }

        // Book the slots
        for (TimeSlot slot : slots) {
            slot.setStatus("BOOKED");
        }

        double totalPrice = slots.stream().mapToDouble(TimeSlot::getPrice).sum();

        Booking booking = new Booking();
        booking.setPrivateMatch(match);
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus("PENDING");
        booking.setIsPaid(false);
        booking.setTotalAmount(totalPrice);

        bookingRepository.save(booking);
        timeSlotRepository.saveAll(slots);

        match.setBooking(booking);
        match.setStatus("CONFIRMED");
        privateMatchRepository.save(match);
    }



}
