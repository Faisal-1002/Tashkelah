package com.example.tuwaiqfinalproject.Model;

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

    @NotEmpty(message = "Match name must not be empty")
    @Column(columnDefinition = "varchar(50) not null")
    private String name;

    @NotEmpty(message = "Status must not be empty")
    @Pattern(
            regexp = "PENDING|SCHEDULED|CONFIRMED",
            message = "Status must be one of: PENDING, SCHEDULED, CONFIRMED"
    )
    @Column(columnDefinition = "varchar(20) not null")
    private String status;

    @OneToOne(mappedBy = "privateMatch", cascade = CascadeType.ALL)
    private Booking booking;

    @OneToMany(mappedBy = "privateMatch", cascade = CascadeType.ALL)
    private List<Emails> emails;

    @OneToOne
    private Player player;

    @ManyToOne
    private Field field;

    @OneToMany(mappedBy = "privateMatch", cascade = CascadeType.ALL)
    private List<TimeSlot> timeSlots;

}
