package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    Booking findBookingById(Integer id);

    @Query("SELECT b FROM Booking b WHERE b.privateMatch.player.id = ?1")
    List<Booking> findPrivateMatchBookingsByPlayerId(Integer playerId);

    @Query("SELECT b FROM Booking b WHERE b.publicMatch.organizer.id = ?1")
    List<Booking> findPublicMatchBookingsByOrganizerId(Integer organizerId);

//    @Query("SELECT b FROM Booking b JOIN b.publicMatch.teamA.players p WHERE p.id = ?1")
//    List<Booking> findPublicMatchBookingsByTeamAPlayerId(Integer playerId);
//
//    @Query("SELECT b FROM Booking b JOIN b.publicMatch.teamB.players p WHERE p.id = ?1")
//    List<Booking> findPublicMatchBookingsByTeamBPlayerId(Integer playerId);
}
