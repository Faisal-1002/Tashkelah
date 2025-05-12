package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
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

    @Column(columnDefinition = "varchar(100)")
    private String name;

    @Column(columnDefinition = "varchar(20)")
    private String number;

    @Column(columnDefinition = "varchar(10)")
    private String cvc;

    @Column(columnDefinition = "varchar(10)")
    private String month;

    @Column(columnDefinition = "varchar(10)")
    private String year;

    @Column(columnDefinition = "double")
    private Double amount;

    @Column(columnDefinition = "varchar(20)")
    private String currency;

    @Column(columnDefinition = "varchar(255)")
    private String description;

    @Column(columnDefinition = "varchar(255)")
    private String callbackUrl;

    @Column(columnDefinition = "varchar(255)")
    private String transactionId;

    @Column(columnDefinition = "time")
    private LocalDateTime payment_date;

    @OneToOne
    @JsonIgnore
    private Booking booking;

}