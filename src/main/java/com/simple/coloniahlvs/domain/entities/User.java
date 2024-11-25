package com.simple.coloniahlvs.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "user")
@Data
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private String email;
    private String DUI;

    //JWToken
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    private List<Token> tokens;

    //TODO immplement join table with flag
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HouseUser> houseUsers;

    //1 vigilante a muchos registros
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Entrance> entrances;

    //muchos usuarios muchos roles
    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "userId"),
            inverseJoinColumns = @JoinColumn(name = "roleId")
    )
    private List<Role> roles;

    //1 residente a un token
    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Residence_Token residenceToken;

    //1 usuario a muchos permisos
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Invitation> gottenInvitations;

    @OneToMany(mappedBy = "host", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Invitation> madeInvitations;

    @JsonIgnore
    @Override
    public String getUsername() {
        return name;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }
    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return true;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority>authorities = new ArrayList<>();
        authorities = roles.stream().map(role -> new SimpleGrantedAuthority(role.getId())).collect(Collectors.toList());
        return authorities;
    }

    @JsonIgnore
    @Override
    public String getPassword() {
        return null;
    }
}
