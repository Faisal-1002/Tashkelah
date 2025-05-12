package com.example.tuwaiqfinalproject.DTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrganizerDTO {
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

    @NotEmpty(message = "address must not be empty")
    private String address;

    @NotEmpty(message = "License number must not be empty")
    private String licence_number;

}
