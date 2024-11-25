package com.simple.coloniahlvs.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "invitation")
public class GracePeriodConfig {
    private Integer gracePeriod;
    private Integer graceTimeQr;

    public Integer getGracePeriod() {
        return gracePeriod;
    }

    public void setGracePeriod(Integer gracePeriod) {
        this.gracePeriod = gracePeriod;
    }

    public Integer getGraceTimeQR() {
        return graceTimeQr;
    }

    public void setGraceTimeQR(Integer graceTimeQr) {
        this.graceTimeQr = graceTimeQr;
    }
}
