package com.example.tuwaiqfinalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Name is required")
    @Column(columnDefinition = "varchar(100) not null")
    private String name;

    @NotEmpty(message = "Card number is required")
    @Column(columnDefinition = "varchar(20) not null")
    private String number;

    @NotEmpty(message = "CVC is required")
    @Column(columnDefinition = "varchar(10) not null")
    private String cvc;

    @NotEmpty(message = "Month is required")
    @Column(columnDefinition = "varchar(10) not null")
    private String month;

    @NotEmpty(message = "Year is required")
    @Column(columnDefinition = "varchar(10) not null")
    private String year;

    @Column(columnDefinition = "double not null")
    private double amount;

    @NotEmpty(message = "Currency is required")
    @Column(columnDefinition = "varchar(10) not null")
    private String currency;

    @NotEmpty(message = "Description is required")
    @Column(columnDefinition = "varchar(255) not null")
    private String description;

    @NotEmpty(message = "Callback URL is required")
    @Column(columnDefinition = "varchar(255) not null")
    private String callbackUrl;

    @OneToOne
    private Booking booking;

}