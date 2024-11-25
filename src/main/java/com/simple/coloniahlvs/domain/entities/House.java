package com.simple.coloniahlvs.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "casa")
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer capacity;
    private String houseNumber;

    //TODO implement join table with flag
    //@JsonIgnore
    @OneToMany(mappedBy = "house", fetch = FetchType.LAZY)
    private List<HouseUser> houseUser;

}
