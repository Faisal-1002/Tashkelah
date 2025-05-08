package com.example.tuwaiqfinalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Amount must not be null")
    @Positive(message = "Amount must be positive")
    @Column(columnDefinition = "double not null")
    private Double amount;

    @NotEmpty(message = "Payment method must not be empty")
    @Column(columnDefinition = "varchar(30) not null")
    private String method; // e.g. CARD, APPLE_PAY, STC_PAY

    @NotNull(message = "Payment date must not be null")
    @Column(columnDefinition = "datetime not null")
    private LocalDateTime date;

    @NotEmpty(message = "Payment status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String status; // e.g. PAID, FAILED

    @OneToOne
    private Booking booking;
}
