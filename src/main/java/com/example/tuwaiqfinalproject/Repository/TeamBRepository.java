package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.TeamB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamBRepository extends JpaRepository<TeamB, Integer > {
    TeamB findTeamBById(Integer id);
}
