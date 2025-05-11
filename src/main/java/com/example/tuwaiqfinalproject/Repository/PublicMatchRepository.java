package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PublicMatchRepository extends JpaRepository<PublicMatch, Integer> {
    PublicMatch findPublicMatchById(Integer id);
    //PublicMatch findBySportAndFieldAndStatus(Sport sport, Field field,String Status);
    List<PublicMatch> findPublicMatchByField(Field field);
    PublicMatch findPublicMatchByFieldAndStatus(Field field,String Status);
    PublicMatch findFirstByFieldAndStatusAndField_Sport(Field field, String status, Sport sport);
    List<PublicMatch>findAllByTeam(Set<Team> teams);

}
