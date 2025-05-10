package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;
import java.util.List;

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
    @Min(value = 2)
    @Max(value = 22)
    @Column(columnDefinition = "int not null")
    private Integer capacity;

    @ManyToOne
    @JsonIgnore
    private Organizer organizer;

    @OneToMany(mappedBy = "field",cascade = CascadeType.ALL)
    private List<PublicMatch> publicMatches;

    @ManyToOne
    private Sport sport;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<PrivateMatch> privateMatches;

}