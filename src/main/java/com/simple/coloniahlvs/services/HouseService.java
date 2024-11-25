package com.simple.coloniahlvs.services;

import com.simple.coloniahlvs.domain.dto.CreateHouseDTO;
import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.HouseUser;
import com.simple.coloniahlvs.domain.entities.User;

import java.util.List;

public interface HouseService {

    void createHouse(CreateHouseDTO createHouseDTO);

    House getHouseByNumber(String houseNumber);

    List<House> getAllHouses();

    void addMember(House house, User user);
    void removeMember(House house, User user);

    List<HouseUser> getListOfMembers(House house);

    void setManager(House house, User user);

    void removeHouse(House house);

    House updateHouse(House house, Integer capacity, User manager);

    List<HouseUser> findMyHouse(User user);
    HouseUser findManagerOfHouse(House house);
    House findHouseByNumber(String houseNumber);
    String getMyHouseNumber(User user);
}
