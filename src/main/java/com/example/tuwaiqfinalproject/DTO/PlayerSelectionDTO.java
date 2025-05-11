package com.example.tuwaiqfinalproject.DTO;

import com.example.tuwaiqfinalproject.Model.TimeSlot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerSelectionDTO {
    //To view all user options with price, stadium image and time

    private String fieldName;
    private String fieldAddress;
    private String selectedTeamName;
    private List<TimeSlot> timeSlots;
    // لصوره



}
