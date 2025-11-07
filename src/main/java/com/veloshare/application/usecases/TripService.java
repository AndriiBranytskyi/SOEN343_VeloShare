package com.veloshare.application.usecases;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.veloshare.application.Result;
import com.veloshare.application.dto.EndTripCmd;
import com.veloshare.application.dto.StartTripCmd;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;
import com.veloshare.domain.Billing;

public class TripService {

    private final bmsService bms;
    private final BillingService billing;
    private final Map <String,Trip> byId=new ConcurrentHashMap<>();

    public TripService(bmsService bms, BillingService billing) {
        this.bms = bms;
        this.billing=billing;
    }

    public Result<String> startTrip(StartTripCmd cmd, User user) {
        try {
            Station s = bms.requireStation(cmd.stationName());
            String tripId = bms.startTrip(cmd.userId(), cmd.bikeId(), s, cmd.estimatedCost(), cmd.estimatedDistance(), user);
            return Result.ok(tripId);
        } catch (IllegalAccessException e) {
            return Result.fail("Forbidden: " + e.getMessage());
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public Result<Billing> endTripAndBill(EndTripCmd cmd, String userId) {
        try {
            Trip trip = getActiveTrip(cmd.tripId());
            if (trip == null) return Result.fail("Trip not found");

            Station end = bms.requireStation(cmd.stationName());
            bms.endTrip(cmd.tripId(), end);

            Billing bill = billing.calculateAndStore(userId, trip);
            return Result.ok(bill);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    private Trip getActiveTrip(String tripId) {
        return bms.getActiveTrip(tripId);
    }
}
