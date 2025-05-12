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

    @PostMapping("/card/{privateMatchId}")
    public ResponseEntity<?> processPayment(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId, @RequestBody Payment paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.processPayment(2, privateMatchId, paymentRequest));
    }

    @GetMapping("/get-status/{privateMatchId}")
    public ResponseEntity<?> getPaymentStatus(@AuthenticationPrincipal User user, @PathVariable Integer privateMatchId) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.getPaymentStatusAndConfirm(2, privateMatchId));
    }
    @PostMapping("/buy")
    public ResponseEntity<?> PublicMatchPayment(@AuthenticationPrincipal User user, @RequestBody Payment paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.PublicMatchPayment(2, paymentRequest));
    }
}
