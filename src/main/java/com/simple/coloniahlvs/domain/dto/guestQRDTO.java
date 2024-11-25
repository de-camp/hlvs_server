package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class guestQRDTO {
    @NotBlank
    private String invitationId;
}
