package com.example.tuwaiqfinalproject.DTO;

import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.TeamA;
import com.example.tuwaiqfinalproject.Model.TeamB;
import com.example.tuwaiqfinalproject.Model.TimeSlot;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicMatchDTO {
// DTO was created to display the teams for this match with their data.


    private TeamA_DTO teamA;


    private TeamB_DTO teamB;
}
