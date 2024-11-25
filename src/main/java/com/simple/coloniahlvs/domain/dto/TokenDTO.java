package com.simple.coloniahlvs.domain.dto;

import com.simple.coloniahlvs.domain.entities.Token;
import lombok.Data;

@Data
public class TokenDTO {

    private String token;
    private String picture;

    public TokenDTO(Token token, String picture) {
        this.token = token.getContent();
        this.picture = picture;
    }

}
