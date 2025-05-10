package com.example.tuwaiqfinalproject.DTO;

import com.example.tuwaiqfinalproject.Model.TeamB;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicMatchDTO {
// DTO was created to display the teams for this match with their data.


    private TeamA_DTO teamA;


    private TeamB_DTO teamB;
}
