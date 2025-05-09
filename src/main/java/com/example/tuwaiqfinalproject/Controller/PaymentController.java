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

    @PostMapping("/card")
    public ResponseEntity<?> processPayment(@AuthenticationPrincipal User user, @RequestBody Payment paymentRequest) {
        return ResponseEntity.status(200).body(paymentService.processPayment(user.getId(), paymentRequest));
    }

    @GetMapping("/get-status/{id}")
    public ResponseEntity<?> getPaymentStatus(@AuthenticationPrincipal User user, @PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.getPaymentStatusAndConfirm(user.getId(), id));
    }
}
