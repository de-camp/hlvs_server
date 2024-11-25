package com.simple.coloniahlvs.services;

import com.simple.coloniahlvs.domain.dto.EntranceDTO;
import com.simple.coloniahlvs.domain.dto.HistoryDTO;
import com.simple.coloniahlvs.domain.entities.Entrance;
import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.Invitation;
import com.simple.coloniahlvs.domain.entities.User;

import java.util.List;

public interface EntranceService {

    User whoEnters(String token);
    Boolean isEntranceValid(User user, String token);
    void registerResidentEntrance(User user);
    void registerGuestEntrance(User user, Invitation invitation);
    Invitation getInvitationByToken(String token);
    Boolean isGuestEntranceValid(User user, String token, Invitation invitation);
    void registerManual(EntranceDTO entranceDTO);
    List<Entrance> getEntrancesByHouse(House house);
    List<HistoryDTO> getWeekHistory();
    List<Entrance> getAllEntrances();
}
