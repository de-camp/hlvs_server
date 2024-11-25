package com.simple.coloniahlvs.controllers;

import com.simple.coloniahlvs.domain.dto.*;
import com.simple.coloniahlvs.domain.entities.*;
import com.simple.coloniahlvs.services.HouseService;
import com.simple.coloniahlvs.services.InvitationService;
import com.simple.coloniahlvs.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invitation")
@Slf4j
public class InvitationController {
    private final UserService userService;
    private final HouseService houseService;
    private final InvitationService invitationService;

    public InvitationController(UserService userService, HouseService houseService, InvitationService invitationService) {
        this.userService = userService;
        this.houseService = houseService;
        this.invitationService = invitationService;
    }

    @PreAuthorize("hasAnyAuthority('VIST')")
    @GetMapping("/mine")
    public ResponseEntity<GeneralResponse> getMyInvitations() {
        User user = userService.findUserAuthenticated();

        if( user == null ) {
            GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        List<InvitationDTO> myInvitations = invitationService.findInvitationsActiveVisitor(user);

        return GeneralResponse.getResponse(HttpStatus.OK, myInvitations);
    }

    @PreAuthorize("hasAnyAuthority('VIST')")
    @GetMapping("/mine/{id}")
    public ResponseEntity<GeneralResponse> getMyInvitation(@PathVariable("id") UUID id){
        User user = userService.findUserAuthenticated();

        if( user == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        InvitationDTO invitation = invitationService.findInvitationByIdAndGuest(id, user);
        if( invitation == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Invitation not found.");
        }

        return GeneralResponse.getResponse(HttpStatus.OK, invitation);
    }

    @PreAuthorize("hasAnyAuthority('REST', 'MANG')")
    @PostMapping("/grant")
    public ResponseEntity<GeneralResponse> grantInvitation(@RequestBody CreateInviteDTO createInviteDTO) {
        User user = userService.findUserAuthenticated();
        User guest = userService.findByIdentifier(createInviteDTO.getEmail());

        if( user == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        if( guest == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "Guest not found.");
        }

        if( !invitationService.hasVisitorRole(guest.getRoles()) ) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "Only guests can be invited.");
        }

        if( invitationService.hasManagerRole(user.getRoles()) ){
            invitationService.createInvitation(createInviteDTO, user, true);
            return GeneralResponse.getResponse("Invitation was granted!");
        }

        List<HouseUser> houseUser =  houseService.findMyHouse(user);
        House house = houseUser.get(0).getHouse();
        HouseUser houseOwner = houseService.findManagerOfHouse(house);

        invitationService.createInvitation(createInviteDTO, houseOwner.getUser(), false);
        return GeneralResponse.getResponse("Invitation request was assigned!");
    }

    @PreAuthorize("hasAnyAuthority('MANG')")
    @GetMapping("/pending")
    public ResponseEntity<GeneralResponse> getPendingInvitations() {
        User user = userService.findUserAuthenticated();

        if( user == null ) {
            GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        List<Invitation> pendingInvitations = invitationService.findInvitationPending(user);

        return GeneralResponse.getResponse(HttpStatus.OK, pendingInvitations);
    }

    @PreAuthorize("hasAnyAuthority('MANG')")
    @PostMapping("/update")
    public ResponseEntity<GeneralResponse> updateInvitation(@RequestBody InvitationUpdateDTO request) {
        User user = userService.findUserAuthenticated();

        if( user == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        if ("accept".equals(request.getAction())){
            invitationService.acceptInvitation(request.getInvitationId(), user);
            return GeneralResponse.getResponse(HttpStatus.OK, "Invitation was accepted!");
        }else {
            invitationService.rejectAndDeleteInvitation(request.getInvitationId(), user);
            return GeneralResponse.getResponse(HttpStatus.OK, "Invitation was cancelled correctly!");
        }
    }

    @PreAuthorize("hasAnyAuthority('MANG')")
    @GetMapping("/approved")
    public ResponseEntity<GeneralResponse> getActiveInvitationsMang() {
        User user = userService.findUserAuthenticated();

        if( user == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        List<Invitation> activeInvitations = invitationService.findInvitationsActiveMangager(user);

        return GeneralResponse.getResponse(HttpStatus.OK, activeInvitations);
    }

    @PreAuthorize("hasAnyAuthority('MANG')")
    @GetMapping("/old")
    public ResponseEntity<GeneralResponse> getOldInvitations() {
        User user = userService.findUserAuthenticated();

        if( user == null ) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found.");
        }

        List<Invitation> invitations = invitationService.findPastInvitations(user);

        return GeneralResponse.getResponse(HttpStatus.OK, invitations);
    }
}
