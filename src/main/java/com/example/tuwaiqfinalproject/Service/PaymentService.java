package com.example.tuwaiqfinalproject.Service;

import com.example.tuwaiqfinalproject.Api.ApiException;
import com.example.tuwaiqfinalproject.Model.*;
import com.example.tuwaiqfinalproject.Repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PlayerRepository playerRepository;
    private final OrganizerRepository organizerRepository;
    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final PublicMatchRepository publicMatchRepository;
    private final PrivateMatchRepository privateMatchRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final WhatsAppService whatsAppService;
    private final EmailsService emailsService;

    @Value("${moyasar.api.key}")
    private String apiKey;
    private static final String MOYASAR_API_URL = "https://api.moyasar.com/v1/payments/";

    // 48. Faisal - Pay - Tested
    public ResponseEntity<String> privateMatchPayment(Integer user_id, Integer privateMatchId, Payment paymentRequest) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null) throw new ApiException("Player not found");

        PrivateMatch match = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (match == null || match.getBooking() == null)
            throw new ApiException("No booking found for this match");

        Booking booking = match.getBooking();
        if (!booking.getStatus().equals("PENDING"))
            throw new ApiException("Booking is already paid or invalid");

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

        String transactionId;
        try {
            // ✅ Extract transaction ID using Jackson
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            transactionId = jsonResponse.get("id").asText();
        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing JSON");
        }

        paymentRequest.setTransactionId(transactionId);
        paymentRequest.setBooking(booking);
        paymentRequest.setPayment_date(LocalDateTime.now());
        paymentRepository.save(paymentRequest);
        // 🧾 Return raw API response
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    // 49. Faisal - Get payment status - Tested
    public String privateMatchPaymentStatus(Integer userId, Integer privateMatchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PrivateMatch match = privateMatchRepository.findPrivateMatchById(privateMatchId);
        if (match == null || match.getBooking() == null)
            throw new ApiException("No booking found for this match");

        Booking booking = match.getBooking();
        if (!booking.getStatus().equals("PENDING"))
            throw new ApiException("Booking is already confirmed or invalid");

        Payment payment = paymentRepository.findPaymentByBooking(booking);
        if (payment == null)
            throw new ApiException("Payment not found");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + booking.getPayment().getTransactionId(),
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String paymentStatus = jsonResponse.get("status").asText();

            if (paymentStatus.equalsIgnoreCase("paid")) {
                booking.setStatus("CONFIRMED");
                booking.setIs_paid(true);
                booking.setPayment(payment);
                bookingRepository.save(booking);

                match.setStatus("CONFIRMED");
                privateMatchRepository.save(match);

                // ✅ Set all time slots to BOOKED
                List<TimeSlot> slots = match.getTime_slots();
                for (TimeSlot slot : slots) {
                    if ("PENDING".equals(slot.getStatus())) {
                        slot.setStatus("BOOKED");
                    }
                }
                timeSlotRepository.saveAll(slots);

                String body = "Hi " + player.getUser().getName() + ",\n"
                        + "Your booking has been confirmed! 🎉\n\n"
                        + "Match Details:\n"
                        + "🏟️ Field: " + match.getField().getName() + "\n"
                        + "📍 Location: " + match.getField().getAddress() + "\n"
                        + "📅 Date: " + match.getTime_slots().get(0).getDate() + "\n"
                        + "⏰ Time: " + match.getTime_slots().get(0).getStart_time() + " – "
                        + match.getTime_slots().get(match.getTime_slots().size() - 1).getEnd_time() + "\n\n"
                        + "Thank you for booking with us. Good luck and enjoy the match! ⚽\n\n"
                        + "- Tashkelah Team";

                whatsAppService.sendMessage(player.getUser().getPhone(), body);

            }

            return response.getBody();

        } catch (Exception e) {
            throw new ApiException("Failed to parse Moyasar response");
        }
    }

    // 35. Eatzaz - Payment - Tested
    public ResponseEntity<String> publicMatchPayment(Integer user_id, Integer publicMatchId, Payment paymentRequest) {
        Player player = playerRepository.findPlayerById(user_id);
        if (player == null) throw new ApiException("Player not found");

        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (match == null)
            throw new ApiException("Public match not found");

        Booking booking = bookingRepository.findByPlayerAndPublicMatch(player, match);
        if (booking == null || !"PENDING".equals(booking.getStatus()))
            throw new ApiException("No pending booking found for this player in the selected match");

        String callbackUrl = "https://dashboard.moyasar.com/entities/f0144c0a-b82c-4fdf-aefb-6c7be5b87cb7/payments";

        paymentRequest.setName(player.getUser().getName());
        paymentRequest.setAmount(booking.getTotal_amount());
        paymentRequest.setCurrency("SAR");

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

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(MOYASAR_API_URL, HttpMethod.POST, entity, String.class);

        String transactionId;
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());
            transactionId = jsonResponse.get("id").asText();
        } catch (JsonProcessingException e) {
            throw new ApiException("Error parsing JSON");
        }

        paymentRequest.setTransactionId(transactionId);
        paymentRequest.setBooking(booking);
        paymentRequest.setPayment_date(LocalDateTime.now());
        paymentRepository.save(paymentRequest);

        notifications(player.getId(),booking.getId());
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    public String publicMatchPaymentStatus(Integer userId, Integer publicMatchId) {
        Player player = playerRepository.findPlayerById(userId);
        if (player == null)
            throw new ApiException("Player not found");

        PublicMatch match = publicMatchRepository.findPublicMatchById(publicMatchId);
        if (match == null)
            throw new ApiException("Public match not found");

        Booking booking = bookingRepository.findByPlayerAndPublicMatch(player, match);
        if (booking == null || !"PENDING".equals(booking.getStatus()))
            throw new ApiException("Booking is already confirmed or invalid");

        Payment payment = paymentRepository.findPaymentByBooking(booking);
        if (payment == null)
            throw new ApiException("Payment not found");

        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(apiKey, "");
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<String> response = restTemplate.exchange(
                MOYASAR_API_URL + payment.getTransactionId(),
                HttpMethod.GET,
                entity,
                String.class
        );

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(response.getBody());

            String paymentStatus = jsonResponse.get("status").asText();

            if ("paid".equalsIgnoreCase(paymentStatus)) {
                booking.setStatus("CONFIRMED");
                booking.setIs_paid(true);
                booking.setPayment(payment);
                bookingRepository.save(booking);

                // ✅ Update all related slots to BOOKED
                List<TimeSlot> slots = match.getTime_slots();
                for (TimeSlot slot : slots) {
                    if ("PENDING".equals(slot.getStatus())) {
                        slot.setStatus("BOOKED");
                    }
                }
                timeSlotRepository.saveAll(slots);
            }

            return response.getBody();

        } catch (Exception e) {
            throw new ApiException("Failed to parse Moyasar response");
        }
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

}