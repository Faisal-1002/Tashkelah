package com.example.tuwaiqfinalproject.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerDTO {

    private Integer id;

    @NotEmpty(message = "Username must not be empty")
    private String username;

    @NotEmpty(message = "Password must not be empty")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "Password must be at least 8 characters and include uppercase, lowercase, number, and special character"
    )
    private String password;

    @NotEmpty(message = "Email must not be empty")
    @Email(message = "Email must be valid")
    private String email;

    @NotEmpty(message = "Name must not be empty")
    private String name;

    @NotEmpty(message = "Phone must not be empty")
    @Pattern(regexp = "^(05)[0-9]{8}$", message = "Phone number must start with 05 and be 10 digits")
    private String phone;

    @NotEmpty(message = "Address must not be empty")
    private String address;

    @NotEmpty(message = "Gender must not be empty")
    @Pattern(regexp = "^(MALE|FEMALE)$", message = "Gender must MALE or FEMALE")
    private String gender;

    @NotNull(message = "Birth date must not be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birth_date;

}
