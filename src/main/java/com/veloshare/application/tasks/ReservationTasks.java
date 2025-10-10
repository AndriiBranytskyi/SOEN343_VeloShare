package com.veloshare.application.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.veloshare.domain.bmsService;

@Component
public class ReservationTasks {

    private final bmsService bms;

    public ReservationTasks(bmsService bms) {
        this.bms = bms;
    }

    @Scheduled(fixedDelay = 30_000)
    public void expireReservations() {
        bms.checkReservations();
    }
}
