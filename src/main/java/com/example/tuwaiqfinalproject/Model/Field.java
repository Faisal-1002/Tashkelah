package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    private String address;

    @Column(columnDefinition = "varchar(255) not null")
    private String description;

    @NotEmpty(message = "Photo must not be empty")
    private String photo;

    @Column(columnDefinition = "time not null")
    private LocalTime open_time;

    @Column(columnDefinition = "time not null")
    private LocalTime close_time;

    @Column(columnDefinition = "int not null")
    private Integer capacity;

    @Column(columnDefinition = "double not null")
    private Double price;

    @ManyToOne
    @JsonIgnore
    private Sport sport;

    @ManyToOne
    @JsonIgnore
    private Organizer organizer;
  
    @OneToMany(mappedBy = "field",cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PublicMatch> public_matches;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<PrivateMatch> private_matches;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL)
    private List<TimeSlot> time_slots;

}