package com.example.tuwaiqfinalproject.DTO;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TeamA_DTO {

    private Integer publicMatch_id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String status;

    @NotEmpty
    private String teamName;

    @NotNull
    private Integer playersCount;



}
