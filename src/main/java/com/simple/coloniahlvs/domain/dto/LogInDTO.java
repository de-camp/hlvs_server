package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LogInDTO {
    @NotBlank
    private String email;
    @NotBlank
    private String name;
}
