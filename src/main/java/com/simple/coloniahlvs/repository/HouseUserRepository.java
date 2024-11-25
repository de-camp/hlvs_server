package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.HouseUser;
import com.simple.coloniahlvs.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HouseUserRepository extends JpaRepository<HouseUser, UUID> {
    HouseUser findByUserAndHouse(User user, House house);
    List<HouseUser> findAllByHouse(House house);

    List<HouseUser> findAllByUser(User user);

    HouseUser findByHouseAndManager(House house, Boolean value);
}
