package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AddMemberDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String houseNumber;
}
