package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.TeamA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamARepository extends JpaRepository<TeamA, Integer> {
    TeamA findTeamAById(Integer id);
    TeamA findTeamAByTeamName(String teamName);
}
