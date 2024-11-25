package com.simple.coloniahlvs.services.implementations;

import com.simple.coloniahlvs.config.GracePeriodConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GracePeriodService {

    private final GracePeriodConfig gracePeriodConfig;

    public GracePeriodService(GracePeriodConfig gracePeriodConfig) {
        this.gracePeriodConfig = gracePeriodConfig;
    }

    public Integer getGracePeriodMinutes() {
        return gracePeriodConfig.getGracePeriod();
    }

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {
        gracePeriodConfig.setGracePeriod(gracePeriodMinutes);
    }

    public Integer getGraceTimeQR() { return gracePeriodConfig.getGraceTimeQR(); }
    public void setGraceTimeQR(Integer graceTimeQR){
        gracePeriodConfig.setGraceTimeQR(graceTimeQR);
    }

}
