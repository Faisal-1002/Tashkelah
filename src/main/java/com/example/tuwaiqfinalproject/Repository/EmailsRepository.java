package com.example.tuwaiqfinalproject.Repository;

import com.example.tuwaiqfinalproject.Model.Emails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmailsRepository extends JpaRepository<Emails, Integer> {
}
