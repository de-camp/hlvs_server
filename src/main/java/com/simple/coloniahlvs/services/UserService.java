package com.simple.coloniahlvs.services;

import com.simple.coloniahlvs.domain.dto.LogInDTO;
import com.simple.coloniahlvs.domain.dto.UserWhoamIDTO;
import com.simple.coloniahlvs.domain.entities.Residence_Token;
import com.simple.coloniahlvs.domain.entities.Token;
import com.simple.coloniahlvs.domain.entities.User;

import java.util.List;

public interface UserService {
    User save(LogInDTO info);
    void changeRoles(User user, List<String> roles);
    User findByIdentifier(String identifier);
    Token registerToken(User user) throws Exception;
    Boolean isTokenValid(User user, String token);
    void cleanTokens(User user) throws Exception;
    List<User> findKeeper();
    User findUserAuthenticated();
    Residence_Token registerResidenceToken(User user) throws Exception;
    Boolean isResidenceTokenValid(User user, String token);
    void eraseToken(Residence_Token token);
    void setDUI(User user, String dui);
}
