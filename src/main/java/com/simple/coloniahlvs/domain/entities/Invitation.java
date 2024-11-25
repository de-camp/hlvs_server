package com.simple.coloniahlvs.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "invitation")
public class Invitation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime starTime;
    private LocalTime finishTime;
    private Boolean exchanged;
    private Boolean status;
    private String invitationType;

    //@JsonIgnore
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "guest_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "host_id", referencedColumnName = "id", nullable = false)
    private User host;


    @OneToMany(mappedBy = "invitation", cascade = CascadeType.ALL)
    private List<WeekDay> weekDays;

    @OneToOne(mappedBy = "invitation")
    @JsonIgnore
    private Residence_Token residenceToken;
}
