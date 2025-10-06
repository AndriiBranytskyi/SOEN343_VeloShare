package com.veloshare.application.usecases;

import com.veloshare.application.Result;
import com.veloshare.application.dto.EndTripCmd;
import com.veloshare.application.dto.StartTripCmd;
import com.veloshare.domain.Station;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;

public class TripService {

    private final bmsService bms;

    public TripService(bmsService bms) {
        this.bms = bms;
    }

    public Result<Void> startTrip(StartTripCmd cmd, User user) {
        try {
            Station s = bms.requireStation(cmd.stationName());
            bms.startTrip(cmd.userId(), cmd.bikeId(), s, cmd.estimatedCost(), cmd.estimatedDistance(), user);
            return Result.ok(null);
        } catch (IllegalAccessException e) {
            return Result.fail("Forbidden: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public Result<Void> endTrip(EndTripCmd cmd) {
        try {
            Station s = bms.requireStation(cmd.stationName());
            bms.endTrip(cmd.tripId(), s);
            return Result.ok(null);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
