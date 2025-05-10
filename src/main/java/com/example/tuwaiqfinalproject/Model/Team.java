package com.example.tuwaiqfinalproject.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Team {

    @Id
    private Integer id;

    private String name;

    private Integer playersCount;

    private Integer max_players_count;

    @OneToOne
    @JsonIgnore
    private PublicMatch public_match;

}
