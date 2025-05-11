package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamRepository extends JpaRepository<Team, Integer> {
    Team findTeamById(Integer id);
}
