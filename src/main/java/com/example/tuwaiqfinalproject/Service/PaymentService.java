package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.Booking;
import com.example.tuwaiqfinalproject.Model.Payment;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.PrivateMatch;
import com.example.tuwaiqfinalproject.Repository.BookingRepository;
import com.example.tuwaiqfinalproject.Repository.PaymentRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PlayerRepository playerRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    @Value("${moyasar.api.key}")
    private String apiKey;
    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    // 27. Faisal - Pay - Tested
    public ResponseEntity<String> processPayment(Integer user_id, Payment paymentRequest) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null) throw new ApiException("Player not found");

        PrivateMatch match = player.getPrivateMatch();
        if (match == null || match.getBooking() == null)
            throw new ApiException("No booking found for this match");

        Booking booking = match.getBooking();
        if (!booking.getStatus().equals("PENDING"))
            throw new ApiException("Booking is already confirmed or invalid");

        String callbackUrl = "https://dashboard.moyasar.com/entities/f0144c0a-b82c-4fdf-aefb-6c7be5b87cb7/payments"; // Replace with your real callback

        paymentRequest.setName(player.getUser().getName());
        paymentRequest.setAmount(booking.getTotalAmount());
        paymentRequest.setCurrency("SAR");

        // 🔒 Moyasar requires amount in the smallest currency unit (e.g. halalas)
        String requestBody = String.format(
                "source[type]=card&source[name]=%s&source[number]=%s&source[cvc]=%s" +
                        "&source[month]=%s&source[year]=%s&amount=%d&currency=%s&callback_url=%s",
                paymentRequest.getName(),
                paymentRequest.getNumber(),
                paymentRequest.getCvc(),
                paymentRequest.getMonth(),
                paymentRequest.getYear(),
                (int) (paymentRequest.getAmount() * 100), // convert SAR to halalah
                paymentRequest.getCurrency(),
                callbackUrl
        );

        // 🔐 Set HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // ⛓ Wrap payload and headers
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

        // 🔁 Send POST request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL, HttpMethod.POST, entity, String.class);
        paymentRequest.setBooking(booking);
        paymentRequest.setPayment_date(LocalDateTime.now());
        paymentRepository.save(paymentRequest);
        // 🧾 Return raw API response
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // 28. Faisal - Get payment status - Tested
    public String getPaymentStatusAndConfirm(Integer user_id, String paymentId) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null)
            throw new ApiException("Player not found");

        PrivateMatch match = player.getPrivateMatch();
        if (match == null || match.getBooking() == null)
            throw new ApiException("No booking found for this match");

        Booking booking = match.getBooking();
        if (!booking.getStatus().equals("PENDING"))
            throw new ApiException("Booking is already confirmed or invalid");

        Payment payment = paymentRepository.findPaymentByBooking(booking);
        if (payment == null)
            throw new ApiException("Payment not found");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, ""); // Moyasar API key as username
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + paymentId,
                HttpMethod.GET,
                entity,
                String.class
        );

        booking.setIsPaid(true);
        booking.setStatus("CONFIRMED");
        booking.setPayment(payment);
        bookingRepository.save(booking);

        return response.getBody();
    }

}
