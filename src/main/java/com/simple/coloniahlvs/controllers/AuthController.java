package com.simple.coloniahlvs.controllers;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.HttpTransport;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.simple.coloniahlvs.domain.dto.*;
import com.simple.coloniahlvs.domain.entities.Token;
import com.simple.coloniahlvs.domain.entities.User;
import com.simple.coloniahlvs.services.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class AuthController {

    @Autowired
    private final UserService userService;

    private final RestTemplate restTemplate;
    @Autowired
    private final Gson gson;

    public AuthController(UserService userService, RestTemplate restTemplate, Gson gson) {
        this.userService = userService;
        this.restTemplate = restTemplate;
        this.gson = gson;
    }

    @PostMapping("/login")
    public ResponseEntity<GeneralResponse> login(@RequestBody @Valid AccessTokenDTO info){

        String url = "https://www.googleapis.com/oauth2/v3/userinfo";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + info.getToken());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        log.info(response.getBody());
        JsonObject jsonObject = gson.fromJson(response.getBody(), JsonObject.class);

        User user = userService.findByIdentifier(jsonObject.get("email").getAsString());

        if(user == null){
            LogInDTO userInfo = new LogInDTO();
            userInfo.setEmail(jsonObject.get("email").getAsString());
            userInfo.setName(jsonObject.get("name").getAsString());

            user = userService.save(userInfo);

            try {
                Token token = userService.registerToken(user);
                return GeneralResponse.getResponse(HttpStatus.OK, new TokenDTO(token, jsonObject.get("picture").getAsString()));
            } catch (Exception e) {
                e.printStackTrace();
                return  GeneralResponse.getResponse(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        try {
            Token token = userService.registerToken(user);
            return GeneralResponse.getResponse(HttpStatus.OK, new TokenDTO(token, jsonObject.get("picture").getAsString()));
        } catch (Exception e) {
            e.printStackTrace();
            return  GeneralResponse.getResponse(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/greeting")
    public ResponseEntity<GeneralResponse> greeting(){
        return GeneralResponse.getResponse(HttpStatus.OK, "Hello World, it is currently running!");
    }


}
