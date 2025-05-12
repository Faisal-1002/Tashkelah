package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertFalse;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Organizer {
    @Id
    private Integer id;

    @Column(columnDefinition = "varchar(10) not null unique")
    private String licence_number;

    @Column(columnDefinition = "varchar(10) not null")
    private String status;

    @OneToOne
    @MapsId
    private User user;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<Field> fields;

    @OneToMany(mappedBy = "organizer", cascade = CascadeType.ALL)
    private List<PublicMatch> public_matches;

}
