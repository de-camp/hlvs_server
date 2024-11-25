package com.simple.coloniahlvs.utils;

import com.simple.coloniahlvs.config.GracePeriodConfig;
import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.User;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class JWTTools {
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.exptime}")
    private Integer exp;

    private  Integer qrexp = 1;

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + exp))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public String generateQRToken(User user, String invitationId, Integer realQrExpTime) {
        Map<String, Object> claims = new HashMap<>();

        return Jwts.builder()
                .claims(claims)
                .subject(user.getEmail() + " " + invitationId)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 60000L*realQrExpTime))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
                .compact();
    }

    public Boolean verifyToken(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build();

            parser.parse(token);
            return true;
        } catch (Exception e) {
//            e.printStackTrace();
            return false;
        }
    }

    public String getUsernameFrom(String token) {
        try {
            JwtParser parser = Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
                    .build();

            return parser.parseSignedClaims(token)
                    .getPayload()
                    .getSubject();
        } catch (Exception e) {
//            e.printStackTrace();
            return null;
        }
    }
}
