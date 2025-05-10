package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
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

    @NotEmpty(message = "Match name must not be empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @NotEmpty(message = "Status must not be empty")
    @Column(columnDefinition = "varchar(20) not null")
    private String status; // e.g. OPEN, FULL

    @ManyToOne
    @JsonIgnore
    private Field field;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "publicMatch")
    @PrimaryKeyJoinColumn
    private Team team;

    @OneToOne(cascade = CascadeType.ALL, mappedBy = "publicMatch")
    @PrimaryKeyJoinColumn
    private PublicMatch publicMatch;

    @ManyToOne
    private Player player;

    @ManyToOne
    private Organizer organizer;

    @OneToMany(mappedBy = "publicMatch", cascade = CascadeType.ALL)
    private List<Booking> bookings;
}
