package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.Token;
import com.simple.coloniahlvs.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID>{
    List<Token> findByUser(User user);
}
