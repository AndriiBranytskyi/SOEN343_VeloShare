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
    private final Map<String, Trip> byId = new ConcurrentHashMap<>();

    public TripService(bmsService bms, BillingService billing) {
        this.bms = bms;
        this.billing = billing;
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

    public Result<Billing> endTripAndBill(EndTripCmd cmd, String userId, boolean operatorActingAsRider) {
        try {
            Trip trip = getActiveTrip(cmd.tripId());
            if (trip == null) {
                return Result.fail("Trip not found");
            }

            Station end = bms.requireStation(cmd.stationName());
            int bikesBefore = end.getBikesAvailable();
            int cap = end.getCapacity();
            double ratioBefore = (cap > 0) ? (double) bikesBefore / (double) cap : 1.0;
            boolean eligibleForFlex = cap > 0 && ratioBefore < 0.25;

            System.out.printf(
                "END_TRIP DEBUG: station=%s bikesBefore=%d capacity=%d ratioBefore=%.3f eligibleForFlex=%s%n",
                end.getName(), bikesBefore, cap, ratioBefore, eligibleForFlex
            );

            bms.endTrip(cmd.tripId(), end);

            if (eligibleForFlex) {
            billing.earnFlexDollars(userId, BillingService.FLEX_DOLLAR_BONUS);
            System.out.println("Flex dollars awarded to user " + userId
                + " (+ " + BillingService.FLEX_DOLLAR_BONUS + ")");
        }
            Billing bill = billing.calculateAndStore(userId, trip, operatorActingAsRider);

            double finalDollars = bill.getAmountCents() / 100.0;
            double baseDollars  = bill.getBaseAmountCents() / 100.0;
            double flexDollars  = bill.getFlexUsedCents() / 100.0;

            trip.setCost(finalDollars);
            trip.setBaseCost(baseDollars);
            trip.setFlexUsed(flexDollars);

            trip.setCost(bill.getAmountCents() / 100.0);
            bms.getRideHistory().recordCompleted(trip);
            return Result.ok(bill);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    public Result<Billing> endTripAndBill(EndTripCmd cmd, String userId) {
        return endTripAndBill(cmd, userId, false);
    }

    private Trip getActiveTrip(String tripId) {
        return bms.getActiveTrip(tripId);
    }
}
