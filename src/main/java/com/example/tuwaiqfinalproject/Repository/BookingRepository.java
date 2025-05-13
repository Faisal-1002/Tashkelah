package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Booking;
import com.example.tuwaiqfinalproject.Model.Player;
import com.example.tuwaiqfinalproject.Model.PublicMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Booking findBookingById(Integer id);
    @Query("SELECT b FROM Booking b WHERE :player MEMBER OF b.public_match.players")
    List<Booking> findBookingsByPlayerInPublicMatch(Player player);
    @Query("SELECT b FROM Booking b WHERE b.player = ?1 AND b.public_match IS NOT NULL")
    List<Booking> findPublicMatchBookingsByPlayer(Player player);
    @Query("SELECT b FROM Booking b WHERE b.player = ?1 AND b.public_match = ?2")
    Booking findByPlayerAndPublicMatch(Player player, PublicMatch match);
}
