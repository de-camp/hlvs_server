package com.simple.coloniahlvs.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
public class CreateInviteDTO {
    @NotBlank
    @Email
    private String email;

    @NotBlank
    private LocalDate startDate;

    @NotBlank
    private LocalDate endDate;

    private List<String> weekDays;

    @NotBlank
    private LocalTime startTime;

    @NotBlank
    private LocalTime endTime;

    @NotBlank
    private String type;
}
