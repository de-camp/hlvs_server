package com.simple.coloniahlvs.controllers;

import com.simple.coloniahlvs.domain.dto.*;
import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.Invitation;
import com.simple.coloniahlvs.domain.entities.Residence_Token;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.services.EntranceService;
import com.simple.coloniahlvs.services.HouseService;
import com.simple.coloniahlvs.services.InvitationService;
import com.simple.coloniahlvs.services.UserService;
import com.simple.coloniahlvs.services.implementations.GracePeriodService;
import com.simple.coloniahlvs.websocket.WebSocketController;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/entrance")
@Slf4j
public class EntranceController {

    @Autowired

    private final UserService userService;
    private final EntranceService entranceService;
    private final InvitationService invitationService;
    private final HouseService houseService;
    private final GracePeriodService gracePeriodService;
    private final WebSocketController webSocketController;

    public EntranceController(UserService userService, EntranceService entranceService, InvitationService invitationService, HouseService houseService, WebSocketController webSocketController, GracePeriodService gracePeriodService) {
        this.userService = userService;
        this.entranceService = entranceService;
        this.invitationService = invitationService;
        this.houseService = houseService;
        this.webSocketController = webSocketController;
        this.gracePeriodService = gracePeriodService;
    }

    @PreAuthorize("hasAnyAuthority('REST','MANG')")
    @PostMapping("/resident/qr")
    public ResponseEntity<GeneralResponse> getResidentQR(){

        User user = userService.findUserAuthenticated();
        if(user == null){
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User not found");
        }

        try {
            Residence_Token token = userService.registerResidenceToken(user);
            return GeneralResponse.getResponse(HttpStatus.OK,new ResidenceTokenDTO(token, gracePeriodService.getGraceTimeQR().toString()));
        } catch (Exception e) {
            e.printStackTrace();
            return GeneralResponse.getResponse("Error generating token");
        }
    }

    @PreAuthorize("hasAnyAuthority('VIST')")
    @PostMapping("/guest/qr")
    public ResponseEntity<GeneralResponse> getGuestQR(@RequestBody @Valid guestQRDTO guestQRDTO){

        User user = userService.findUserAuthenticated();
        if(user == null){
            return GeneralResponse.getResponse("User not found");
        }
        //TODO: validate if the user is the one invited
        Invitation invitation = invitationService.getInvitationById(guestQRDTO.getInvitationId());
        if(invitation == null){
            return GeneralResponse.getResponse("Invitation not found");
        }

        if (!invitationService.validateTime(invitation)){
            return GeneralResponse.getResponse("Not in time");
        }

        try{
            Residence_Token token = invitationService.registerInvitationToken(user, invitation);
            return GeneralResponse.getResponse(HttpStatus.OK, new ResidenceTokenDTO(token, gracePeriodService.getGraceTimeQR().toString()));

        }catch (Exception e){
            e.printStackTrace();
            return GeneralResponse.getResponse("Error generating token");
        }
    }

    @PreAuthorize("hasAnyAuthority('MANG')")
    @GetMapping("/house-history")
    public ResponseEntity<GeneralResponse> getHouseHistory(){
        User user = userService.findUserAuthenticated();
        //TODO: pagination
        return GeneralResponse.getResponse(HttpStatus.OK,
                entranceService.getEntrancesByHouse(user.getHouseUsers().get(0).getHouse()));
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @GetMapping("/history")
    public ResponseEntity<GeneralResponse> getHistory(){
        return GeneralResponse.getResponse(HttpStatus.OK, entranceService.getAllEntrances());
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @GetMapping("/recent")
    public ResponseEntity<GeneralResponse> getRecent(){
        return GeneralResponse.getResponse(HttpStatus.OK, entranceService.getWeekHistory());

    }

    @PreAuthorize("hasAnyAuthority('KEEP')")
    @PostMapping("/validate-entrance")
    public ResponseEntity<GeneralResponse> validateEntrance(@RequestBody AccessTokenDTO accessTokenDTO) {
        User user = entranceService.whoEnters(accessTokenDTO.getToken());
        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "Token no valido");
        }

        if(user.getRoles().stream().anyMatch(role -> role.getId().equals("REST") || role.getId().equals("MANG"))){

            if (!entranceService.isEntranceValid(user, accessTokenDTO.getToken())) {
                return GeneralResponse.getResponse(HttpStatus.UNAUTHORIZED, "Token no valido");
            }
            entranceService.registerResidentEntrance(user);
            userService.eraseToken(user.getResidenceToken());

            //
            if(accessTokenDTO.getDoor() == 1)
                webSocketController.sendMessage("porton");
            else if(accessTokenDTO.getDoor() == 2)
                webSocketController.sendMessage("puerta");
            //

            return GeneralResponse.getResponse(HttpStatus.OK, "Entrada registrada");

        } else if (user.getRoles().stream().anyMatch(role -> role.getId().equals("VIST"))) {
            Invitation invitation = entranceService.getInvitationByToken(accessTokenDTO.getToken());
            if (invitation == null) {
                return GeneralResponse.getResponse(HttpStatus.UNAUTHORIZED, "Token no valido");
            }

            if (!entranceService.isGuestEntranceValid(user, accessTokenDTO.getToken(), invitation)) {
                return GeneralResponse.getResponse(HttpStatus.UNAUTHORIZED, "Token no valido");
            }

            if (!invitationService.validateTime(invitation)){
                return GeneralResponse.getResponse(HttpStatus.UNAUTHORIZED, "Not in time");
            }

            entranceService.registerGuestEntrance(user, invitation);
            invitationService.deleteToken(invitation.getResidenceToken());

            //
            if(accessTokenDTO.getDoor() == 1)
                webSocketController.sendMessage("porton");
            else if(accessTokenDTO.getDoor() == 2)
                webSocketController.sendMessage("puerta");
            //

            return GeneralResponse.getResponse(HttpStatus.OK, "Entrada registrada");

        }else {
            return GeneralResponse.getResponse(HttpStatus.UNAUTHORIZED, "No tiene permisos para entrar, solo residentes o visitantes con invitación");
        }

    }

    @PreAuthorize("hasAnyAuthority('KEEP')")
    @PostMapping("/manual-entry")
    public ResponseEntity<GeneralResponse> manualEntry(@RequestBody EntranceDTO entranceDTO) {
        User user = userService.findUserAuthenticated();

        House house = houseService.findHouseByNumber(entranceDTO.getHouseNumber());
        if (house == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House not found");
        }
        entranceService.registerManual(entranceDTO);

        //
        if(entranceDTO.getDoor() == 1)
            webSocketController.sendMessage("porton");
        else if(entranceDTO.getDoor() == 2)
            webSocketController.sendMessage("puerta");
        //
        return GeneralResponse.getResponse(HttpStatus.OK , "Entrada registrada");
    }

}
