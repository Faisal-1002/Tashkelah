package com.example.tuwaiqfinalproject.Controller;

import com.example.tuwaiqfinalproject.Model.Payment;
import com.example.tuwaiqfinalproject.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/card")
    public ResponseEntity<String> processPayment(@RequestBody Payment paymentRequest) {
        return paymentService.processPayment(paymentRequest);
    }

    @GetMapping("/get-status/{id}")
    public ResponseEntity<?> getPaymentStatus(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(paymentService.getPaymentStatus(id));
    }
}
