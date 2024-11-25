package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.Invitation;
import com.simple.coloniahlvs.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.StyledEditorKit;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface InvitationRepository extends JpaRepository<Invitation, UUID> {
    // Guest
    //Get Invitations active
    List<Invitation> findAllByExchangedAndEndDateAfterAndUserAndStatus(Boolean exchanged, LocalDate endDate, User user, Boolean status);
    Invitation findByIdAndUser(UUID id, User user);
    Invitation findByIdAndHost(UUID id, User user);

    List<Invitation> findAllByExchangedAndStatusAndEndDateAfterAndHost(Boolean exchanged, Boolean status, LocalDate date, User user);
    
    // Find invitations active, date sent is yesterday so invitations from today and on are returned
    List<Invitation> findAllByExchangedAndEndDateAfter(boolean exchanged, LocalDate endDate);

    // Manager only
    // Find invitations exchanged, date sent is tomorrow so invitations from today and on are returned
    List<Invitation> findAllByEndDateBeforeAndHost(LocalDate endDate, User user);
    // Find invitations pending for approval
    List<Invitation> findAllByStatusAndHost(Boolean status, User user);

}