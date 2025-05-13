package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Player {
    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(10) not null")
    private String gender;

    @Column(columnDefinition = "date not null")
    private LocalDate birth_date;

    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "player")
    @JsonIgnore
    private List<PrivateMatch> private_matches;

    @ManyToOne
    @JsonIgnore
    private PublicMatch public_match;

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    private List<Booking> bookings;

}
