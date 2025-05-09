package com.example.tuwaiqfinalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Booking time must not be null")
    @Column(columnDefinition = "datetime not null")
    private LocalDateTime bookingTime;

    @NotEmpty(message = "Status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String status; // CONFIRMED, CANCELLED, etc.

    @NotNull(message = "isPaid must not be null")
    @Column(columnDefinition = "boolean not null")
    private Boolean isPaid;

    @Column(columnDefinition = "double not null")
    private Double totalAmount;

    @OneToOne
    private PrivateMatch privateMatch;

    @ManyToOne
    private PublicMatch publicMatch;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;
}
