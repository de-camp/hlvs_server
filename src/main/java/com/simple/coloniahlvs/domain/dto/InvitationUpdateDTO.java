package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class InvitationUpdateDTO {
    @NotBlank
    private UUID invitationId;
    @NotBlank
    private String action;
}
