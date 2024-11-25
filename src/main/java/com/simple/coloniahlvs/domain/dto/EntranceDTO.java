package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EntranceDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String DUI;
    @NotBlank
    private String Comment;
    @NotBlank
    private String houseNumber;

    private Integer door;
}
