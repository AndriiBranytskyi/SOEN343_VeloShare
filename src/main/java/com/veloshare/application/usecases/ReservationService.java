package com.veloshare.application.usecases;

import com.veloshare.application.Result;
import com.veloshare.application.dto.ReserveBikeCmd;
import com.veloshare.domain.Station;
import com.veloshare.domain.bmsService;

public class ReservationService {

    private final bmsService bms;

    public ReservationService(bmsService bms) {
        this.bms = bms;
    }

    public Result<Void> reserve(ReserveBikeCmd cmd) {
        try {
            Station s = bms.requireStation(cmd.StationName());
            bms.reserveBike(cmd.userId(), cmd.bikeId(), cmd.minutes(), s);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> cancel(String reservationId) {
        try {
            bms.cancelReservation(reservationId);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }

    }
}
