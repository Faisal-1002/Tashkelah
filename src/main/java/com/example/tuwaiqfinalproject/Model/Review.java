package com.example.tuwaiqfinalproject.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotEmpty(message = "The comment must not be Empty")
    @Column(columnDefinition = "varchar(200) not null")
    private String comment;

    @NotEmpty(message = "Please Enter descriptive feedback")
    @Pattern(regexp = "ممتاز|لا بأس به|جيد جدا|جيد")
    @Column(columnDefinition = "varchar(15) not null")
    private String assessment_Level;

    @ManyToOne
    @JsonIgnore
    private Field field;
}
