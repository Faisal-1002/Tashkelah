package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Model.Payment;
import com.example.tuwaiqfinalproject.Model.User;
import com.example.tuwaiqfinalproject.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    //PLAYER
    @PostMapping("/pay-private-match/{privateMatchId}")
    public ResponseEntity<?> privateMatchPayment(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId, @RequestBody Payment paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.privateMatchPayment(user.getId(), privateMatchId, paymentRequest));
    }

    //PLAYER
    @GetMapping("/get-private-match-status/{privateMatchId}")
    public ResponseEntity<?> privateMatchPaymentStatus(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.privateMatchPaymentStatus(user.getId(), privateMatchId));
    }

    //PLAYER
    @PostMapping("/pay-public-match/{publicMatchId}")
    public ResponseEntity<?> publicMatchPayment(@AuthenticationPrincipal User user, @PathVariable Integer publicMatchId, @RequestBody Payment paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.publicMatchPayment(user.getId(), publicMatchId, paymentRequest));
    }

    //PLAYER
    @GetMapping("/get-public-match-status/{publicMatchId}")
    public ResponseEntity<?> publicMatchPaymentStatus(@AuthenticationPrincipal User user, @PathVariable Integer publicMatchId) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.publicMatchPaymentStatus(user.getId(), publicMatchId));
    }
}
