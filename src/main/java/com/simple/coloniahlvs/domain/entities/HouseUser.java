package com.simple.coloniahlvs.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "house_user")
public class HouseUser {
    @Id
    @JsonIgnore
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private boolean manager;

    @ManyToOne(fetch = FetchType.EAGER)
    @JsonIgnore
    private House house;

    @ManyToOne(fetch = FetchType.EAGER)
    private User user;

}
