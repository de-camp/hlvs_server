package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.Entrance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface EntranceRepository extends JpaRepository<Entrance, UUID> {
    List<Entrance> findByHouseNumber(String houseNumber);
    List<Entrance> findAllByOrderByDateDesc();
    Integer countEntrancesByDateBetween(LocalDateTime start, LocalDateTime end);
}
