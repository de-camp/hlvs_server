package com.simple.coloniahlvs.domain.dto;

import lombok.Data;

@Data
public class GoogleResponse {
    private String email;
    private boolean email_verified;
    private String name;
    private String error;
}
