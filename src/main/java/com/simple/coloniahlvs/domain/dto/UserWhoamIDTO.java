package com.simple.coloniahlvs.domain.dto;

import com.simple.coloniahlvs.domain.entities.Role;
import lombok.Data;

import java.util.List;

@Data
public class UserWhoamIDTO {
    private String name;
    private String email;
    private String DUI;
    private List<Role> roles;
}
