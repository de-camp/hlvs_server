package com.simple.coloniahlvs.domain.dto;

import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.domain.entities.WeekDay;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
public class InvitationDTO {
    private UUID id;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime starTime;
    private LocalTime finishTime;
    private String invitationType;
    private InvitationUserDTO guest;
    private InvitationUserDTO host;
    private List<WeekDay> weekDays;
    private String houseNumber;
}
