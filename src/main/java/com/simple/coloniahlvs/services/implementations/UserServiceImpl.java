package com.simple.coloniahlvs.services.implementations;

import com.simple.coloniahlvs.domain.dto.LogInDTO;
import com.simple.coloniahlvs.domain.dto.UserWhoamIDTO;
import com.simple.coloniahlvs.domain.entities.Residence_Token;
import com.simple.coloniahlvs.domain.entities.Role;
import com.simple.coloniahlvs.domain.entities.Token;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.repository.ResidenceTokenRepository;
import com.simple.coloniahlvs.repository.RoleRepository;
import com.simple.coloniahlvs.repository.TokenRepository;
import com.simple.coloniahlvs.repository.UserRepository;
import com.simple.coloniahlvs.services.UserService;
import com.simple.coloniahlvs.utils.JWTTools;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final JWTTools jwtTools;
    private final TokenRepository tokenRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RoleRepository roleRepository;
    @Autowired
    private final ResidenceTokenRepository residenceTokenRepository;
    private final GracePeriodService gracePeriodService;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ResidenceTokenRepository residenceTokenRepository, JWTTools jwtTools, TokenRepository tokenRepository, GracePeriodService gracePeriodService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.residenceTokenRepository = residenceTokenRepository;
        this.jwtTools = jwtTools;
        this.tokenRepository = tokenRepository;
        this.gracePeriodService = gracePeriodService;
    }


    @Override
    public User save(LogInDTO info) {
        User user = new User();

        user.setEmail(info.getEmail());
        user.setName(info.getName());
        Role visitor = roleRepository.findById("VIST").orElse(null);
        List<Role> roles = new ArrayList<>();
        roles.add(visitor);
        if (visitor != null) {
            user.setRoles(roles);
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public void changeRoles(User user, List<String> roles) {
        List<Role>  rolesFound = roleRepository.findAllById(roles);

        user.setRoles(rolesFound);
        userRepository.save(user);
    }

    @Override
    public User findByIdentifier(String identifier) {
        return userRepository.findByNameOrEmail(identifier, identifier);
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Token registerToken(User user) throws Exception {
        cleanTokens(user);

        String tokenString = jwtTools.generateToken(user);
        Token token = new Token(tokenString, user);

        tokenRepository.save(token);

        return token;
    }

    @Override
    public Boolean isTokenValid(User user, String token) {
        try {
            cleanTokens(user);
            List<Token> tokens = tokenRepository.findByUser(user);

            tokens.stream()
                    .filter(tk -> tk.getContent().equals(token))
                    .findAny()
                    .orElseThrow(() -> new Exception());

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void cleanTokens(User user) throws Exception {
        List<Token> tokens = tokenRepository.findByUser(user);

        tokens.forEach(token -> {
            if(!jwtTools.verifyToken(token.getContent())) {
                token.setActive(false);
                tokenRepository.save(token);
            }
        });

    }

    @Override
    public List<User> findKeeper() {
        Role role = roleRepository.findById("KEEP").orElse(null);
        List<Role> aux = new ArrayList<>();
        aux.add(role);

        return userRepository.findByRoles(aux);
    }

    @Override
    public User findUserAuthenticated() {
        String name = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        User user = userRepository.findByNameOrEmail(name, name);
        return user;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Residence_Token registerResidenceToken(User user) throws Exception {

        Residence_Token Actual = user.getResidenceToken();

        String tokenString = jwtTools.generateQRToken(user, "resident", gracePeriodService.getGraceTimeQR());
        Residence_Token token = new Residence_Token(tokenString, user);
        if (Actual != null){
            Actual.setContent(token.getContent());
            Actual.setTimeStamp(token.getTimeStamp());
            residenceTokenRepository.save(Actual);
        } else {
            residenceTokenRepository.save(token);
        }

        return token;
    }

    @Override
    public Boolean isResidenceTokenValid(User user, String token) {

        try {
            Residence_Token tokens = residenceTokenRepository.findByUser(user);

            if (!tokens.getContent().equals(token)){
                throw new Exception();
            }

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void eraseToken(Residence_Token token) {
        token.setContent("");
        residenceTokenRepository.save(token);
    }

    @Override
    public void setDUI(User user, String dui) {
        user.setDUI(dui);
        userRepository.save(user);
    }
}
