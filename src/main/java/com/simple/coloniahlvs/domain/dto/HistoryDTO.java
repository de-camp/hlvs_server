package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class HistoryDTO {
    @NotBlank
    private String day;
    private Integer entrances;

    public HistoryDTO(String day, Integer entrances) {
        this.day = day;
        this.entrances = entrances;
    }
}
