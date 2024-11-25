package com.simple.coloniahlvs.controllers;

import com.simple.coloniahlvs.domain.dto.ChangeRoleDTO;
import com.simple.coloniahlvs.domain.dto.DuiDTO;
import com.simple.coloniahlvs.domain.dto.GeneralResponse;
import com.simple.coloniahlvs.domain.dto.UserWhoamIDTO;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/update-dui")
    public ResponseEntity<GeneralResponse> updateMyDUI(@RequestBody DuiDTO DUI) {
        userService.setDUI(userService.findUserAuthenticated(), DUI.getDui());
        return GeneralResponse.getResponse("DUI successfully updated!");
    }

    @PreAuthorize("hasAnyAuthority('SUDO','MANG')")
    @PostMapping("/set-role")
    public ResponseEntity<GeneralResponse> setRole(@RequestBody @Valid ChangeRoleDTO changeRoleDTO) {

        User user = userService.findByIdentifier(changeRoleDTO.getIdentifier());

        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND,"User not found!");
        }

        userService.changeRoles(user, changeRoleDTO.getRole());
        return GeneralResponse.getResponse(HttpStatus.OK ,"Role successfully updated!");
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @GetMapping("/keeper")
    public ResponseEntity<GeneralResponse> getKeeper() {

        return GeneralResponse.getResponse(HttpStatus.OK, userService.findKeeper());
    }

    @GetMapping("/whoami")
    public ResponseEntity<GeneralResponse> whoAmI() {

        User user = userService.findUserAuthenticated();

        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.NOT_FOUND, "User not found!");
        }
      
        UserWhoamIDTO response = new UserWhoamIDTO();
        response.setName(user.getName());
        response.setRoles(user.getRoles());
        response.setEmail(user.getEmail());
        response.setDUI(user.getDUI());

        return GeneralResponse.getResponse(HttpStatus.OK, response);
    }
}
