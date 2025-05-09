package com.example.tuwaiqfinalproject.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Emails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Email(message = "Must be a valid email")
    @NotEmpty(message = "Email cannot be empty")
    @Column(columnDefinition = "varchar(100) not null")
    private String email;

    @ManyToOne
    private PrivateMatch privateMatch;
}
