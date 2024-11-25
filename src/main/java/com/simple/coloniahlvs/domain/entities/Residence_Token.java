package com.simple.coloniahlvs.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "residence_token")
public class Residence_Token {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "code")
    private UUID code;

    private String content;

    @Column(name = "timestamp")
    private LocalDateTime timeStamp;

    @Column(name = "active")
    private Boolean active;



    //1 residente a 1 token
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "resident_id", referencedColumnName = "id")
    @JsonIgnore
    private User user;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "entrance_id", referencedColumnName = "id")
    @JsonIgnore
    private Invitation invitation;

    public Residence_Token(String content, User user) {
        super();
        this.content = content;
        this.user = user;
        this.timeStamp = LocalDateTime.now();
        this.active = true;
    }

    public Residence_Token(String content, Invitation invitation) {
        super();
        this.content = content;
        this.invitation = invitation;
        this.timeStamp = LocalDateTime.now();
        this.active = true;
    }
}
