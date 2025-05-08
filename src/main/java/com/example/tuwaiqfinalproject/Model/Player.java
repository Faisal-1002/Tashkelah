package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Past;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate birthDate;

    @OneToOne
    @MapsId
    @JsonIgnore
    private User user;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    private PrivateMatch privateMatch;

    @ManyToOne
    private PublicMatch publicMatch;
}
