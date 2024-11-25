package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DuiDTO {
    @NotBlank
    private String dui;
}
