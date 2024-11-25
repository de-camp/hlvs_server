package com.simple.coloniahlvs.repository;

import com.simple.coloniahlvs.domain.entities.Role;
import com.simple.coloniahlvs.domain.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    User findByNameOrEmail(String name, String email);

    List<User> findByRoles(List<Role> roles);
    List<User> findAllByRolesIs(List<Role> roles);

}
