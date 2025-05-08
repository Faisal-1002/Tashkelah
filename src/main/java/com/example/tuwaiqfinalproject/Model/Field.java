package com.example.tuwaiqfinalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Field {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @Column(columnDefinition = "varchar(100) not null")
    private String location;

    @Column(columnDefinition = "varchar(200) not null")
    private String description;

    @NotEmpty(message = "Photo URL must not be empty")
    @Column(columnDefinition = "varchar(255) not null")
    private String photo;

    @Column(columnDefinition = "time not null")
    private LocalTime openTime;

    @Column(columnDefinition = "time not null")
    private LocalTime closeTime;

    @NotNull(message = "capacity must not be empty")
    @Column(columnDefinition = "capacity not null")
    @Min(value = 2)
    @Max(value = 22)
    private Integer capacity;

    @ManyToOne
    private Organizer organizer;

    @ManyToOne
    private Sport sport;
}