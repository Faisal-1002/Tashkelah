package com.example.tuwaiqfinalproject.Model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class TeamB {

    @Id
    private Integer id;

    private String teamName;

    private Integer playersCount;


    @OneToOne
    @JsonIgnore
    private PublicMatch publicMatch;
}
