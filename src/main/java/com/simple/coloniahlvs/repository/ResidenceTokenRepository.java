package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.Invitation;
import com.simple.coloniahlvs.domain.entities.Residence_Token;
import com.simple.coloniahlvs.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResidenceTokenRepository extends JpaRepository<Residence_Token, UUID> {

    Residence_Token findByUser(User user);
    Residence_Token findByInvitation(Invitation invitation);
}
