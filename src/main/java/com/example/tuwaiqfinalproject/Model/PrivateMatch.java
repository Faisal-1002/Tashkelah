package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
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
public class PrivateMatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "Status must not be empty")
    @Pattern(
            regexp = "CREATED|PENDING|SCHEDULED|CONFIRMED",
            message = "Status must be one of: CREATED, PENDING, SCHEDULED, CONFIRMED"
    )
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @OneToOne(mappedBy = "private_match", cascade = CascadeType.ALL)
    private Booking booking;

    @OneToMany(mappedBy = "private_match", cascade = CascadeType.ALL)
    private List<Emails> emails;

    @OneToOne
    private Player player;

    @ManyToOne
    @JsonIgnore
    private Field field;

    @OneToMany(mappedBy = "private_match", cascade = CascadeType.ALL)
    private List<TimeSlot> time_slots;
}
