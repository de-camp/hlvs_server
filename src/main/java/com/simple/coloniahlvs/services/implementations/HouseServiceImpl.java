package com.simple.coloniahlvs.services.implementations;

import com.simple.coloniahlvs.domain.dto.CreateHouseDTO;
import com.simple.coloniahlvs.domain.entities.House;
import com.simple.coloniahlvs.domain.entities.HouseUser;
import com.simple.coloniahlvs.domain.entities.Role;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.repository.HouseRepository;
import com.simple.coloniahlvs.repository.HouseUserRepository;
import com.simple.coloniahlvs.repository.RoleRepository;
import com.simple.coloniahlvs.repository.UserRepository;
import com.simple.coloniahlvs.services.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HouseServiceImpl implements HouseService{

    @Autowired
    private final HouseRepository houseRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final HouseUserRepository houseUserRepository;

    @Autowired
    private final RoleRepository roleRepository;

    public HouseServiceImpl(
            HouseRepository houseRepository, UserRepository userRepository,
            HouseUserRepository houseUserRepository, RoleRepository roleRepository
    ) {
        this.houseRepository = houseRepository;
        this.userRepository = userRepository;
        this.houseUserRepository = houseUserRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public void createHouse(CreateHouseDTO createHouseDTO) {
        House house = new House();

        house.setHouseNumber(createHouseDTO.getNumber());
        house.setCapacity(createHouseDTO.getCapacity());

        houseRepository.save(house);
    }

    @Override
    public House getHouseByNumber(String houseNumber) {
        return houseRepository.findByHouseNumber(houseNumber);
    }

    @Override
    public List<House> getAllHouses() {
        return houseRepository.findAll();
    }

    @Override
    public void addMember(House house, User user) {
        HouseUser houseUser = new HouseUser();
        houseUser.setHouse(house);
        houseUser.setUser(user);
        houseUser.setManager(false);

        List<HouseUser> houseUsers = new ArrayList<>();
        houseUsers.add(houseUser);
        houseUserRepository.save(houseUser);

        List<Role> roles = new ArrayList<>();
        Role role = roleRepository.findById("REST").orElse(null);
        roles.add(role);
        user.setRoles(roles);
        user.setHouseUsers(houseUsers);
        userRepository.save(user);
    }

    @Override
    public void removeMember(House house, User user) {
        HouseUser houseUser = houseUserRepository.findByUserAndHouse(user, house);
        houseUserRepository.delete(houseUser);

        List<Role> roles = new ArrayList<>();
        Role role = roleRepository.findById("VIST").orElse(null);
        roles.add(role);
        user.setRoles(roles);
        userRepository.save(user);
    }

    @Override
    public List<HouseUser> getListOfMembers(House house) {
        return houseUserRepository.findAllByHouse(house);

    }

    @Override
    public void setManager(House house, User user) {
        List<HouseUser> houseUsers = houseUserRepository.findAllByHouse(house);

        for (HouseUser houseUser : houseUsers) {
            if (houseUser.getUser().equals(user)) {
                houseUser.setManager(true);
                houseUserRepository.save(houseUser);

                User userAux = houseUser.getUser();
                List<Role> roles = new ArrayList<>();
                Role role = roleRepository.findById("MANG").orElse(null);
                roles.add(role);
                userAux.setRoles(roles);
                userRepository.save(userAux);

            } else if(houseUser.isManager()){
                houseUser.setManager(false);
                houseUserRepository.save(houseUser);

                User userAux = houseUser.getUser();
                List<Role> roles = new ArrayList<>();
                Role role = roleRepository.findById("REST").orElse(null);
                roles.add(role);
                userAux.setRoles(roles);
                userRepository.save(userAux);
            }
        }
    }

    @Override
    public void removeHouse(House house) {
        List<HouseUser> houseUsers = getListOfMembers(house);

        for (HouseUser houseUser : houseUsers) {
            User user = houseUser.getUser();
            List<Role> roles = new ArrayList<>();
            Role role = roleRepository.findById("VIST").orElse(null);
            roles.add(role);
            user.setRoles(roles);
            userRepository.save(user);
        }

        houseUserRepository.deleteAll(houseUsers);
        houseRepository.delete(house);


    }

    @Override
    public House updateHouse(House house, Integer capacity, User manager) {
        house.setCapacity(capacity);
        setManager(house, manager);
        houseRepository.save(house);

        return house;
    }

    @Override
    public List<HouseUser> findMyHouse(User user) {
        return houseUserRepository.findAllByUser(user);
    }

    @Override
    public HouseUser findManagerOfHouse(House house) {
        return houseUserRepository.findByHouseAndManager(house, true);
    }

    @Override
    public House findHouseByNumber(String houseNumber) {
        return houseRepository.findByHouseNumber(houseNumber);
    }

    @Override
    public String getMyHouseNumber(User user) {
        return user.getHouseUsers().get(0).getHouse().getHouseNumber();
    }
}
