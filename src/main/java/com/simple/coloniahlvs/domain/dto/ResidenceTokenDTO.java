package com.simple.coloniahlvs.domain.dto;

import com.simple.coloniahlvs.domain.entities.Residence_Token;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResidenceTokenDTO {
    private String token;
    private String graceTime;

    public ResidenceTokenDTO(Residence_Token token, String graceTime) {
        this.token = token.getContent();
        this.graceTime = graceTime;
    }
}
