package com.simple.coloniahlvs.controllers;

import com.simple.coloniahlvs.domain.dto.AddMemberDTO;
import com.simple.coloniahlvs.domain.dto.CreateHouseDTO;
import com.simple.coloniahlvs.domain.dto.GeneralResponse;
import com.simple.coloniahlvs.domain.dto.UpdateHouseDTO;
import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.services.HouseService;
import com.simple.coloniahlvs.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/house")
@Slf4j
public class HouseController {

    private final HouseService houseService;
    private final UserService userService;

    public HouseController(HouseService houseService, UserService userService) {
        this.houseService = houseService;
        this.userService = userService;
    }
    @PreAuthorize("hasAnyAuthority('MANG', 'SUDO')")
    @PostMapping("/add-member")
    public ResponseEntity<GeneralResponse> addMember(@RequestBody AddMemberDTO member) {
        User user = userService.findByIdentifier(member.getEmail());

        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User not found!");
        } else if (user.getRoles().stream().anyMatch(role -> role.getId().equals("SUDO"))){
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "Admin cannot be a member!");
        }

        House house = houseService.getHouseByNumber(member.getHouseNumber());

        if (house == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House not found!");
        } else if (house.getCapacity() == house.getHouseUser().size()) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House is full!");
        }else if (houseService.getListOfMembers(house).stream().anyMatch(u -> u.getUser().equals(user))) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User is already a member!");
        }

        houseService.addMember(house, user);

        return GeneralResponse.getResponse(HttpStatus.OK,"Member added!");
    }

    @PreAuthorize("hasAnyAuthority('MANG', 'SUDO')")
    @PostMapping("/remove-member")
    public ResponseEntity<GeneralResponse> removeMember(@RequestBody AddMemberDTO member) {

        User user = userService.findByIdentifier(member.getEmail());
        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User not found!");
        }

        House house = houseService.getHouseByNumber(member.getHouseNumber());
        if (house == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House not found!");
        }else if (houseService.getListOfMembers(house).stream().noneMatch(u -> u.getUser().equals(user))) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User is not a member!");
        }
        houseService.removeMember(house, user);
        return GeneralResponse.getResponse("Member removed!");
    }

    @PreAuthorize("hasAnyAuthority('MANG')")
    @GetMapping("/mine")//todo: Denys
    public ResponseEntity<GeneralResponse> getMyHouse() {
        User user = userService.findUserAuthenticated();
        return GeneralResponse.getResponse(HttpStatus.OK, houseService.getHouseByNumber(houseService.getMyHouseNumber(user)));
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @PostMapping("/create")
    public ResponseEntity<GeneralResponse> createHouse(@RequestBody @Valid CreateHouseDTO createHouseDTO) {

        House house = houseService.getHouseByNumber(createHouseDTO.getNumber());

        if (house != null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House already exists!");
        }

        houseService.createHouse(createHouseDTO);
        return GeneralResponse.getResponse(HttpStatus.OK, "House created!");
    }

    @PreAuthorize("hasAnyAuthority('SUDO','KEEP')")
    @GetMapping("/all")
    public ResponseEntity<GeneralResponse> getAllHouses() {
        return GeneralResponse.getResponse(HttpStatus.OK, houseService.getAllHouses());
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @GetMapping("/{number}")
    public ResponseEntity<GeneralResponse> getHouse(@PathVariable String number) {
        return GeneralResponse.getResponse(HttpStatus.OK, houseService.getHouseByNumber(number));
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @PostMapping("/delete/{number}")
    public ResponseEntity<GeneralResponse> deleteHouse(@PathVariable String number) {
        House house = houseService.getHouseByNumber(number);

        if (house == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House not found!");
        }
        houseService.removeHouse(house);
        return GeneralResponse.getResponse("House deleted!");
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @PostMapping("/update")
    public ResponseEntity<GeneralResponse> updateHouse(@RequestBody UpdateHouseDTO updateHouseDTO){
        User user = userService.findByIdentifier(updateHouseDTO.getManagerEmail());
        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User not found!");
        }
        House house = houseService.getHouseByNumber(updateHouseDTO.getHouseNumber());
        if (house == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House not found!");
        }else if (houseService.getListOfMembers(house).stream().noneMatch(u -> u.getUser().equals(user))) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User is not a member!");
        }

        if (updateHouseDTO.getCapacity() < house.getHouseUser().size()) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "Capacity cannot be less than the number of members!");
        }

        return GeneralResponse.getResponse(HttpStatus.OK, "House updated!", houseService.updateHouse(house, updateHouseDTO.getCapacity(), user));
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @PostMapping("/set-manager")
    public ResponseEntity<GeneralResponse> setManager(@RequestBody AddMemberDTO member) {

        User user = userService.findByIdentifier(member.getEmail());
        if (user == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User not found!");
        }

        House house = houseService.getHouseByNumber(member.getHouseNumber());
        if (house == null) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "House not found!");
        } else if (houseService.getListOfMembers(house).stream().noneMatch(u -> u.getUser().equals(user))) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User is not a member!");
        } else if (house.getHouseUser().stream().anyMatch(houseUser -> houseUser.isManager() && houseUser.getUser().equals(user))) {
            return GeneralResponse.getResponse(HttpStatus.BAD_REQUEST, "User is already a manager!");
        }

        houseService.setManager(house, user);

        return GeneralResponse.getResponse(HttpStatus.OK, "Manager set!");
    }


}
