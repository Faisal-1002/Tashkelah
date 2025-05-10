package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
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
//Solve the problem of circular relationships between the user object and the player
    @Override
    public int hashCode() {
        return Objects.hash(id); // استخدام حقل id فقط لتوليد الـ hashCode
    }

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    private PrivateMatch private_match;

    @ManyToOne
    private PublicMatch public_match;

}
