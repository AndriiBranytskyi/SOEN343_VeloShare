package com.veloshare.application.usecases;

import com.veloshare.application.Result;
import com.veloshare.application.dto.MoveBikeCmd;
import com.veloshare.domain.Station;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;

public class OperatorService {

    private final bmsService bms;

    public OperatorService(bmsService bms) {
        this.bms = bms;
    }

    public Result<Void> moveBike(MoveBikeCmd cmd, User user) {
        try {
            Station from = bms.requireStation(cmd.fromStation());
            Station to = bms.requireStation(cmd.toStation());
            bms.moveBike(cmd.bikeId(), from, to, user);
            return Result.ok(null);
        } catch (IllegalAccessException e) {
            return Result.fail("Forbidden: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> setStationOutOfService(String stationName, boolean oos, User operator) {
        try {
            Station s = bms.requireStation(stationName);
            bms.setStationOutOfService(s, oos);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> setBikeMaintenance(String bikeId, boolean maintenance, User operator) {
        try {
            bms.setBikeMaintenance(bikeId, maintenance);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
