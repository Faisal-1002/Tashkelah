package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    private LocalDateTime booking_time;

    @NotEmpty(message = "Status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @Column(columnDefinition = "boolean not null")
    private Boolean is_paid = false;

    @Column(columnDefinition = "double not null")
    private Double total_amount;

    @OneToOne
    @JsonIgnore
    private PrivateMatch private_match;

    @ManyToOne
    @JsonIgnore
    private PublicMatch public_match;

    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
    private Payment payment;

}
