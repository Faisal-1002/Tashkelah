package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.BookingRepository;
import com.example.tuwaiqfinalproject.Repository.PaymentRepository;
import com.example.tuwaiqfinalproject.Repository.PlayerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        PrivateMatch match = player.getPrivate_match();
        if (match == null || match.getBooking() == null)
            throw new ApiException("No booking found for this match");

        Booking booking = match.getBooking();
        if (!booking.getStatus().equals("PENDING"))
            throw new ApiException("Booking is already confirmed or invalid");

        String callbackUrl = "https://dashboard.moyasar.com/entities/f0144c0a-b82c-4fdf-aefb-6c7be5b87cb7/payments"; // Replace with your real callback

        paymentRequest.setName(player.getUser().getName());
        paymentRequest.setAmount(booking.getTotal_amount());
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

        PrivateMatch match = player.getPrivate_match();
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

        booking.setIs_paid(true);
        booking.setStatus("CONFIRMED");
        booking.setPayment(payment);
        bookingRepository.save(booking);

        return response.getBody();
    }
    //10. Eatzaz -Payment-  need test
    public ResponseEntity<String> PublicMatchPayment(Integer user_id, Payment paymentRequest) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null) throw new ApiException("Player not found");

        PublicMatch match = player.getPublic_match();
        if (match == null || match.getBookings() == null || match.getBookings().isEmpty())
            throw new ApiException("No booking found for this match");

        List<Booking> pendingBookings = match.getBookings().stream()
                .filter(b -> "PENDING".equals(b.getStatus()))
                .collect(Collectors.toList());

        if (pendingBookings.isEmpty()) {
            throw new ApiException("No pending bookings found for this match");
        }
        String callbackUrl = "https://dashboard.moyasar.com/entities/f0144c0a-b82c-4fdf-aefb-6c7be5b87cb7/payments";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);


        ResponseEntity<String> response = null;

        for (Booking booking : pendingBookings) {
            double amount = booking.getTotal_amount();

            String requestBody = String.format(
                    "source[type]=card&source[name]=%s&source[number]=%s&source[cvc]=%s" +
                            "&source[month]=%s&source[year]=%s&amount=%d&currency=%s&callback_url=%s",
                    paymentRequest.getName(),
                    paymentRequest.getNumber(),
                    paymentRequest.getCvc(),
                    paymentRequest.getMonth(),
                    paymentRequest.getYear(),
                    (int) (amount * 100), // إلى هللة
                    "SAR",
                    callbackUrl
            );

            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
            response = restTemplate.exchange(MOYASAR_API_URL, HttpMethod.POST, entity, String.class);

            // حفظ كل دفعه
            Payment newPayment = new Payment();
            newPayment.setBooking(booking);
            newPayment.setName(player.getUser().getName());
            newPayment.setAmount(amount);
            newPayment.setCurrency("SAR");
            newPayment.setPayment_date(LocalDateTime.now());

            paymentRepository.save(newPayment);
        }
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}