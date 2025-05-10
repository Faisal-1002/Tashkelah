package com.example.tuwaiqfinalproject.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Team_DTO {

    private Integer public_match_id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String status;

    @NotNull
    private Integer players_count;

    @NotNull
    private Integer max_players_count;

}
