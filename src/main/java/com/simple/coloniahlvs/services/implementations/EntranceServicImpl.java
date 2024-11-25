package com.simple.coloniahlvs.services.implementations;

import com.simple.coloniahlvs.domain.dto.EntranceDTO;
import com.simple.coloniahlvs.domain.dto.HistoryDTO;
import com.simple.coloniahlvs.domain.entities.*;
import com.simple.coloniahlvs.repository.EntranceRepository;
import com.simple.coloniahlvs.repository.InvitationRepository;
import com.simple.coloniahlvs.repository.ResidenceTokenRepository;
import com.simple.coloniahlvs.repository.UserRepository;
import com.simple.coloniahlvs.services.EntranceService;
import com.simple.coloniahlvs.services.UserService;
import com.simple.coloniahlvs.utils.JWTTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EntranceServicImpl implements EntranceService {

    private final UserService userService;
    private final JWTTools jwtTools;
    private final ResidenceTokenRepository residenceTokenRepository;
    private final InvitationRepository invitationRepository;
    private final EntranceRepository entranceRepository;
    private final UserRepository userRepository;

    public EntranceServicImpl(ResidenceTokenRepository residenceTokenRepository, JWTTools jwtTools, UserService userService, InvitationRepository invitationRepository, EntranceRepository entranceRepository, UserRepository userRepository) {
        this.residenceTokenRepository = residenceTokenRepository;
        this.jwtTools = jwtTools;
        this.userService = userService;
        this.invitationRepository = invitationRepository;
        this.entranceRepository = entranceRepository;
        this.userRepository = userRepository;
    }
    @Override
    public User whoEnters(String token) {
        String data = jwtTools.getUsernameFrom(token);
        log.info("data: " + data);
        if (data == null){return null;}
        String[] email = data.split(" ", 2);
        //jwt ya valida que el token sea valido!!!
        log.info("email: " + email[0]);
        User user = userService.findByIdentifier(email[0]);
        if (user == null){return null;}
        //valido que sea el toquen del que tengo registro
        return user;
    }

    @Override
    public Boolean isEntranceValid(User user, String token) {
        try {
            Residence_Token registered = residenceTokenRepository.findByUser(user);

            if (!registered.getContent().equals(token)){
                throw new Exception("Token not valid");
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void registerResidentEntrance(User user) {

        Entrance entrance = new Entrance();
        //entrance.setUser(user);//TODO cambiar por el del vigilante
        entrance.setComment("Entrada de residente");
        entrance.setDui(user.getDUI());
        entrance.setHouseNumber(user.getHouseUsers().get(0).getHouse().getHouseNumber());
        entrance.setNombre(user.getName());
        entrance.setDate(LocalDateTime.now());
        entranceRepository.save(entrance);
    }

    @Override
    public void registerGuestEntrance(User user, Invitation invitation) {
        Entrance entrance = new Entrance();
//        entrance.setUser(user);//TODO cambiar por el del vigilante
        entrance.setComment("Entrada de invitado");
        entrance.setDui(user.getDUI());
        entrance.setHouseNumber(invitation.getHost().getHouseUsers().get(0).getHouse().getHouseNumber());
        entrance.setNombre(user.getName());
        entrance.setDate(LocalDateTime.now());
        entranceRepository.save(entrance);
    }

    @Override
    public Invitation getInvitationByToken(String token) {
        String data = jwtTools.getUsernameFrom(token);
        if (data == null){return null;}
        String[] invitation = data.split(" ", 2);
        //jwt ya valida que el token sea valido!!!
        return invitationRepository.findById(UUID.fromString(invitation[1])).orElse(null);
    }

    @Override
    public Boolean isGuestEntranceValid(User user, String token, Invitation invitation) {
        try {
            Residence_Token registered = residenceTokenRepository.findByInvitation(invitation);

            if (!registered.getContent().equals(token)){
                throw new Exception("Token not valid");
            }
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void registerManual(EntranceDTO entranceDTO) {
        //TODO set vigilante
        Entrance entrance = new Entrance();
        entrance.setComment(entranceDTO.getComment());
        entrance.setDui(entranceDTO.getDUI());
        entrance.setHouseNumber(entranceDTO.getHouseNumber());
        entrance.setNombre(entranceDTO.getName());
        entrance.setDate(LocalDateTime.now());
        entranceRepository.save(entrance);
    }

    @Override
    public List<Entrance> getEntrancesByHouse(House house) {
        return entranceRepository.findByHouseNumber(house.getHouseNumber());
    }

    @Override
    public List<HistoryDTO> getWeekHistory() {
        List<HistoryDTO> history = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDateTime start = LocalDate.now().minusDays(i).atStartOfDay();
            LocalDateTime end = LocalDate.now().minusDays(i).atTime(23, 59, 59);
//            log.info("date: " + date);
//            log.info(date.getDayOfWeek().toString());
            history.add(new HistoryDTO(start.getDayOfWeek().toString(), entranceRepository.countEntrancesByDateBetween(start, end)));
        }
        return history;
    }

    @Override
    public List<Entrance> getAllEntrances() {
        return entranceRepository.findAllByOrderByDateDesc();
    }
}
