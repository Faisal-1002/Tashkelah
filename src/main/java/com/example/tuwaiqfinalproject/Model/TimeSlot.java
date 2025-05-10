package com.example.tuwaiqfinalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Date must not be null")
    @Column(columnDefinition = "date not null")
    private LocalDate date;

    @NotNull(message = "Start time must not be null")
    @Column(columnDefinition = "time not null")
    private LocalTime start_time;

    @NotNull(message = "End time must not be null")
    @Column(columnDefinition = "time not null")
    private LocalTime end_time;

    @NotNull(message = "Price must not be null")
    @Min(value = 0, message = "Price must be non-negative")
    @Column(columnDefinition = "double not null")
    private Double price;

    @NotEmpty(message = "Status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @ManyToOne
    @JsonIgnore
    private Field field;

    @ManyToOne
    @JsonIgnore
    private PrivateMatch privateMatch;

    @ManyToOne
    @JsonIgnore
    private PublicMatch publicMatch;


}
