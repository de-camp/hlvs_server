package com.simple.coloniahlvs.services.implementations;

import com.simple.coloniahlvs.domain.dto.CreateInviteDTO;
import com.simple.coloniahlvs.domain.dto.InvitationDTO;
import com.simple.coloniahlvs.domain.dto.InvitationUserDTO;
import com.simple.coloniahlvs.domain.entities.Invitation;
import com.simple.coloniahlvs.domain.entities.Role;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.domain.entities.WeekDay;
import com.simple.coloniahlvs.repository.InvitationRepository;
import com.simple.coloniahlvs.repository.UserRepository;
import com.simple.coloniahlvs.services.InvitationService;
import com.simple.coloniahlvs.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import com.simple.coloniahlvs.domain.entities.Residence_Token;
import com.simple.coloniahlvs.repository.ResidenceTokenRepository;
import com.simple.coloniahlvs.utils.JWTTools;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class InvitationServiceImpl implements InvitationService {
    private final UserRepository userRepository;
    private final InvitationRepository invitationRepository;
    private final JWTTools jwtTools;
    private final ResidenceTokenRepository residenceTokenRepository;
    @Autowired
    private GracePeriodService gracePeriodService;

    public InvitationServiceImpl(UserRepository userRepository, InvitationRepository invitationRepository, JWTTools jwtTools, ResidenceTokenRepository residenceTokenRepository) {
        this.userRepository = userRepository;
        this.invitationRepository = invitationRepository;
        this.jwtTools = jwtTools;
        this.residenceTokenRepository = residenceTokenRepository;
    }

    private List<WeekDay> convertToWeekDays(Invitation invite, List<String> weekDays) {
        List<WeekDay> weekDayEntities = new ArrayList<>();
        for (String day : weekDays) {
            WeekDay weekDayEntity = new WeekDay();
            weekDayEntity.setDayOfWeek(day);
            weekDayEntity.setInvitation(invite);
            weekDayEntities.add(weekDayEntity);
        }
        return weekDayEntities;
    }

    public boolean hasManagerRole(List<Role> roles) {
        return roles.stream().anyMatch(role -> "MANG".equals(role.getId()));
    }

    public boolean hasVisitorRole(List<Role> roles) {
        return roles.stream().anyMatch(role -> "VIST".equals(role.getId()));
    }

    @Override
    public void createInvitation(CreateInviteDTO invitation, User user, Boolean isManager) {
        // New Invitation
        Invitation invite = new Invitation();

        invite.setHost(user);
        invite.setUser(userRepository.findByNameOrEmail(invitation.getEmail(), invitation.getEmail()));
        invite.setStartDate(invitation.getStartDate());
        invite.setEndDate(invitation.getEndDate());
        invite.setStarTime(invitation.getStartTime());
        invite.setFinishTime(invitation.getEndTime());
        invite.setInvitationType(invitation.getType());
        invite.setStatus(isManager);
        invite.setExchanged(false);

        if (invitation.getType().equals("recurrente")) {
            invite.setWeekDays(convertToWeekDays(invite, invitation.getWeekDays()));
        }

        invitationRepository.save(invite);
    }

    // This service gets the invitation where guest is the user sent as parameter, invitation has not been exchanged and also has been approved
    // NOTE: A recurrent invitation exchanged value cannot be changed to true if it's used once
    @Override
    public List<InvitationDTO> findInvitationsActiveVisitor(User user) {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Invitation> invitationsActivePre = invitationRepository
                .findAllByExchangedAndEndDateAfterAndUserAndStatus(false, today.minusDays(1), user, true);

        List<Invitation> filteredInvitations = new ArrayList<>();
        for (Invitation invitation : invitationsActivePre) {
            LocalDate endDate = invitation.getEndDate();
            LocalTime finishTime = invitation.getFinishTime();

            if( !endDate.equals(today) || (endDate.equals(today) && finishTime.isAfter(currentTime.minusMinutes(gracePeriodService.getGracePeriodMinutes()+1))) ){
                filteredInvitations.add(invitation);
            }
        }

        List<InvitationDTO> invitationDTOs = new ArrayList<>();
        for (Invitation invitation : filteredInvitations){
            InvitationDTO invitationDTO = new InvitationDTO();

            invitationDTO.setId(invitation.getId());
            invitationDTO.setStartDate(invitation.getStartDate());
            invitationDTO.setEndDate(invitation.getEndDate());
            invitationDTO.setStarTime(invitation.getStarTime());
            invitationDTO.setFinishTime(invitation.getFinishTime());
            invitationDTO.setInvitationType(invitation.getInvitationType());

            InvitationUserDTO guestDTO = new InvitationUserDTO();
            guestDTO.setName(invitation.getUser().getName());
            guestDTO.setEmail(invitation.getUser().getEmail());
            invitationDTO.setHouseNumber(invitation.getHost().getHouseUsers().get(0).getHouse().getHouseNumber());
            invitationDTO.setGuest(guestDTO);

            InvitationUserDTO hostDTO = new InvitationUserDTO();
            hostDTO.setName(invitation.getHost().getName());
            hostDTO.setEmail(invitation.getHost().getEmail());
            invitationDTO.setHost(hostDTO);

            invitationDTO.setWeekDays(invitation.getWeekDays());
            invitationDTOs.add(invitationDTO);
        }

        return invitationDTOs;
    }

    @Override
    public InvitationDTO findInvitationByIdAndGuest(UUID id, User user) {
        Invitation invitation = invitationRepository.findByIdAndUser(id, user);

        InvitationDTO invitationDTO = new InvitationDTO();
        invitationDTO.setId(invitation.getId());
        invitationDTO.setStartDate(invitation.getStartDate());
        invitationDTO.setEndDate(invitation.getEndDate());
        invitationDTO.setStarTime(invitation.getStarTime());
        invitationDTO.setFinishTime(invitation.getFinishTime());
        invitationDTO.setInvitationType(invitation.getInvitationType());

        InvitationUserDTO guestDTO = new InvitationUserDTO();
        guestDTO.setName(invitation.getUser().getName());
        guestDTO.setEmail(invitation.getUser().getEmail());
        invitationDTO.setHouseNumber(invitation.getHost().getHouseUsers().get(0).getHouse().getHouseNumber());
        invitationDTO.setGuest(guestDTO);

        InvitationUserDTO hostDTO = new InvitationUserDTO();
        hostDTO.setName(invitation.getHost().getName());
        hostDTO.setEmail(invitation.getHost().getEmail());
        invitationDTO.setHost(hostDTO);

        invitationDTO.setWeekDays(invitation.getWeekDays());

        return invitationDTO;
    }

    @Override
    public List<Invitation> findPastInvitations(User user) {
        LocalDate date = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Invitation> pastInvitations = invitationRepository.findAllByEndDateBeforeAndHost( date.plusDays(1), user);

        List<Invitation> filteredInvitations = new ArrayList<>();
        for (Invitation invitation : pastInvitations) {
            LocalDate endDate = invitation.getEndDate();
            LocalTime finishTime = invitation.getFinishTime();

            if( !endDate.equals(date) || (endDate.equals(date) && finishTime.isBefore(currentTime)) ){
                filteredInvitations.add(invitation);
            }
        }

        return filteredInvitations;
    }
  
    @Override
    public Invitation getInvitationById(String id) {
        return invitationRepository.findById(UUID.fromString(id)).orElse(null);
    }

    @Override
    public List<Invitation> findInvitationPending(User user) {
        return invitationRepository.findAllByStatusAndHost(false, user);
    }

    @Override
    public void acceptInvitation(UUID invitationId, User user) {
        Invitation invitation = invitationRepository.findByIdAndHost(invitationId, user);
        // Hacer validaciones en caso no se encuentre la invitacion

        if(!invitation.getExchanged()){
            invitation.setStatus(true);
            invitationRepository.save(invitation);
        }
    }

    @Override
    public void rejectAndDeleteInvitation(UUID invitationId, User user) {
        Invitation invitation = invitationRepository.findByIdAndHost(invitationId, user);
        // Hacer validaciones en caso no se encuentre la invitacion

        invitationRepository.delete(invitation);
    }

    @Override
    public List<Invitation> findInvitationsActiveMangager(User user) {
        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        List<Invitation> activeInvitations =
                invitationRepository.findAllByExchangedAndStatusAndEndDateAfterAndHost(false, true, today.minusDays(1), user);

        List<Invitation> filteredInvitations = new ArrayList<>();
        for (Invitation invitation : activeInvitations) {
            LocalDate endDate = invitation.getEndDate();
            LocalTime finishTime = invitation.getFinishTime();

            if( !endDate.equals(today) || (endDate.equals(today) && finishTime.isAfter(currentTime.minusMinutes(gracePeriodService.getGracePeriodMinutes()+1))) ){
                filteredInvitations.add(invitation);
            }
        }

        return filteredInvitations;
    }

    @Override
    public Residence_Token registerInvitationToken(User user, Invitation invitation) throws Exception {

        Residence_Token Actual = invitation.getResidenceToken();

        String tokenString = jwtTools.generateQRToken(user, invitation.getId().toString(), gracePeriodService.getGraceTimeQR());
        Residence_Token generated = new Residence_Token(tokenString, invitation);
        if (Actual != null) {
            Actual.setContent(tokenString);
            Actual.setTimeStamp(LocalDateTime.now());
            residenceTokenRepository.save(Actual);
            return Actual;
        }else {
            residenceTokenRepository.save(generated);
        }
        return generated;
      }

    @Override
    public Boolean validateTime(Invitation invitation) {

        LocalTime now = LocalTime.now();
        LocalDate today = LocalDate.now();
        Integer gracePeriod = gracePeriodService.getGracePeriodMinutes();

        LocalDateTime ActualNow = LocalDateTime.now();

        LocalDateTime AuxStartDateTime = LocalDateTime.of(today, invitation.getStarTime());
        LocalDateTime AuxFinishDateTime = LocalDateTime.of(today, invitation.getFinishTime());

        if(ActualNow.isBefore(AuxStartDateTime.minusMinutes(gracePeriod))){
            return false;
        }
        if(ActualNow.isAfter(AuxFinishDateTime.plusMinutes(gracePeriod))){
            return false;
        }
        if (today.isBefore(invitation.getStartDate()) || today.isAfter(invitation.getEndDate())) {
            return false;
        }

        return true;
    }

    @Override
    public void deleteToken(Residence_Token token) {
        token.setContent("");
        residenceTokenRepository.save(token);
    }

}
