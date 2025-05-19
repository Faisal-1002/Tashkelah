package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Field;
import com.example.tuwaiqfinalproject.Model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Player findPlayerById(Integer id);
    @Query("select p from PublicMatch m join m.players p where p.id=?1 and m.field=?2")
    Player findPlayedOnField(Integer playerId, Field field);
}
