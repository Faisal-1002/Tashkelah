package com.example.tuwaiqfinalproject.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalTime;

@Data
@AllArgsConstructor
public class FieldDTO {

    @NotEmpty(message = "Field name must not be empty")
    private String name;

    @NotEmpty(message = "Location must not be empty")
    private String location;

    @NotEmpty(message = "Description must not be empty")
    private String description;

    @NotNull(message = "Open time must not be null")
    private LocalTime openTime;

    @NotNull(message = "Close time must not be null")
    private LocalTime closeTime;

    @NotNull(message = "capacity must not be empty")
    private Integer capacity;
}
