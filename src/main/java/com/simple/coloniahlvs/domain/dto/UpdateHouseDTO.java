package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateHouseDTO {
    @NotNull
    private Integer capacity;
    @NotNull
    private String houseNumber;
    @NotBlank
    private String managerEmail;
}
