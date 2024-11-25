package com.simple.coloniahlvs.services;


import com.simple.coloniahlvs.domain.dto.CreateInviteDTO;
import com.simple.coloniahlvs.domain.dto.InvitationDTO;
import com.simple.coloniahlvs.domain.entities.Invitation;
import com.simple.coloniahlvs.domain.entities.Role;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.domain.entities.Residence_Token;
import java.util.UUID;
import java.util.List;

public interface InvitationService {
    void createInvitation(CreateInviteDTO invitation, User user, Boolean isManager);
    List<Invitation> findPastInvitations(User user);
    List<InvitationDTO> findInvitationsActiveVisitor(User user);
    InvitationDTO findInvitationByIdAndGuest(UUID id, User user);
    boolean hasManagerRole(List<Role> roles);
    boolean hasVisitorRole(List<Role> roles);
    Invitation getInvitationById(String id);
    List<Invitation> findInvitationPending(User user);
    void acceptInvitation(UUID invitationId, User user);
    void rejectAndDeleteInvitation(UUID invitationId, User user);
    List<Invitation> findInvitationsActiveMangager(User user);
    Residence_Token registerInvitationToken(User user, Invitation invitation) throws Exception;
    Boolean validateTime(Invitation invitation);
    void deleteToken(Residence_Token token);
}
