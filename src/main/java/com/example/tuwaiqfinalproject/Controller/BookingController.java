package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Api.ApiResponse;
import com.example.tuwaiqfinalproject.Model.Booking;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.status(200).body(bookingService.getAllBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBookingById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(bookingService.getBookingById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal User user) {
        List<Booking> myBookings = bookingService.getMyBookings(user.getId());
        return ResponseEntity.status(200).body(myBookings);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBooking(@PathVariable Integer id, @RequestBody @Valid Booking booking) {
        bookingService.updateBooking(id, booking);
        return ResponseEntity.status(200).body(new ApiResponse("Booking updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBooking(@PathVariable Integer id) {
        bookingService.deleteBooking(id);
        return ResponseEntity.status(200).body(new ApiResponse("Booking deleted successfully"));
    }

    @PostMapping("/book/private-match")
    public ResponseEntity<?> bookPrivateMatch(@AuthenticationPrincipal User user, @RequestBody List<Integer> slotIds) {
        bookingService.bookPrivateMatch(user.getId(), slotIds);
        return ResponseEntity.status(200).body(new ApiResponse("Private match booked successfully"));
    }
    @PostMapping("/book/publicMatch")
    public ResponseEntity<?> bookPublicMatch(@AuthenticationPrincipal User user, @RequestBody List<Integer> slotIds) {
        bookingService.bookPublicMatch(user.getId(), slotIds);
        return ResponseEntity.status(200).body(new ApiResponse("Public match booked successfully"));
    }

    @GetMapping("getBookingPublicMatch")
    public ResponseEntity getMyBookingForPublicMatch(@AuthenticationPrincipal User user){
        return ResponseEntity.status(200).body(bookingService.getMyBookingForPublicMatch(user.getId()));
    }

}
