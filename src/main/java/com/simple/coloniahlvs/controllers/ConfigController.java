package com.simple.coloniahlvs.controllers;

import com.simple.coloniahlvs.config.GracePeriodConfig;
import com.simple.coloniahlvs.domain.dto.EntranceGracePeriodDTO;
import com.simple.coloniahlvs.domain.dto.GeneralResponse;
import com.simple.coloniahlvs.domain.dto.QRGracePeriodDTO;
import com.simple.coloniahlvs.services.implementations.GracePeriodService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/config")
@Slf4j
public class ConfigController {
    @Autowired
    private GracePeriodService gracePeriodService;

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @GetMapping("/qr-period")
    public ResponseEntity<GeneralResponse> getQRPeriod(){
        return GeneralResponse.getResponse(HttpStatus.OK, gracePeriodService.getGraceTimeQR());
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @PostMapping("/qr-period")
    public ResponseEntity<GeneralResponse> updateQRPeriod(@RequestBody QRGracePeriodDTO minutesQR) {
        gracePeriodService.setGraceTimeQR(minutesQR.getCodes());
        return GeneralResponse.getResponse(HttpStatus.OK, "New QR period set!");
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @GetMapping("/entrace-grace-period")
    public ResponseEntity<GeneralResponse> getGracePeriod(){
        return GeneralResponse.getResponse(HttpStatus.OK, gracePeriodService.getGracePeriodMinutes());
    }

    @PreAuthorize("hasAnyAuthority('SUDO')")
    @PostMapping("/entrace-grace-period")
    public ResponseEntity<GeneralResponse> updateGracePeriod(@RequestBody EntranceGracePeriodDTO minutesGracePeriod) {
        gracePeriodService.setGracePeriodMinutes(minutesGracePeriod.getMinutesGracePeriod());
        return GeneralResponse.getResponse(HttpStatus.OK, "New entrace grace period set!");
    }
}

