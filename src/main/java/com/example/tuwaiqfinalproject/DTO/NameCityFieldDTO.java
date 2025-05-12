package com.example.tuwaiqfinalproject.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NameCityFieldDTO {
    @NotEmpty(message = "Field name must not be empty")
    private String name;
    @NotEmpty(message = "Address must not be empty")
    private String address;
    @NotEmpty(message = "Photo must not be empty")
    private String photo;
}
