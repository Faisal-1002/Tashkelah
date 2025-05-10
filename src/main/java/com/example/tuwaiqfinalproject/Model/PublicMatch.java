package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PublicMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Match name must not be empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @NotEmpty(message = "Status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    @Pattern(regexp = "PENDING|OPEN|FULL")
    private String status; // e.g. OPEN, FULL

    @ManyToOne
    @JsonIgnore
    private Field field;

    @OneToMany(mappedBy = "public_match", cascade = CascadeType.ALL)
    private List<TimeSlot> time_slots;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "public_match")
    @PrimaryKeyJoinColumn
    private Team team;

    @OneToMany(cascade = CascadeType.ALL,mappedBy = "public_match")
    private Set<Player> players;

    @ManyToOne
    @JsonIgnore
    private Organizer organizer;

    @OneToMany(mappedBy = "public_match", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
