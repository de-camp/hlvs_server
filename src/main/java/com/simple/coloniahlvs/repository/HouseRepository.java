package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.House;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface HouseRepository extends JpaRepository<House, UUID> {
    House findByHouseNumber(String houseNumber);

    House findHouseByHouseNumberEquals(String houseNumber);
}
