package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PublicMatch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Status must not be empty")
    @Pattern(regexp = "OPEN|FULL", message = "Status must be OPEN or FULL")
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @ManyToOne
    @JsonIgnore
    private Field field;

    @OneToMany(mappedBy = "public_match", cascade = CascadeType.ALL)
    private List<TimeSlot> time_slots;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "public_match")
    private List<Team> teams;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "public_match")
    private List<Player> players;

    @ManyToOne
    @JsonIgnore
    private Organizer organizer;

    @OneToMany(mappedBy = "public_match", cascade = CascadeType.ALL)
    private List<Booking> bookings;

}
