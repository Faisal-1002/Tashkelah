package com.example.tuwaiqfinalproject.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSelectionDTO {
    //To view all user options with price, stadium image and time

    private String fieldName;
    private String fieldLocation;
    private Double price;
    private String teamName;
    private LocalDate date;
    private LocalTime startTime;
    // لصوره



}
