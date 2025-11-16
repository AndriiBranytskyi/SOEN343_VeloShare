package com.veloshare.application.usecases;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.veloshare.domain.Billing;
import com.veloshare.domain.Station;
import com.veloshare.domain.Trip;

public class BillingService {

    // userId -> bills 
    private final Map<String, List<Billing>> byUser = new ConcurrentHashMap<>();
    // tripId -> bill
    private final Map<String, Billing> byTrip = new ConcurrentHashMap<>();

    // safely get station name
    private static String stationName(Station s) {
        return (s == null) ? null : s.getName();
    }

    // billing preview for popup before confirming payment
    public Billing preview(String userId, Trip trip, String arrivalStation, Date endTime) {
        Date start = trip.getStartTime();
        if (endTime == null) {
            endTime = new Date();
        }

        long durationMs = Math.max(0, endTime.getTime() - start.getTime());
        long minutes = Math.max(1, (long) Math.ceil(durationMs / 60000.0));
        int amountCents = Billing.BASE_FEE_CENTS + (int) (minutes * Billing.PER_MINUTE_FEE_CENTS);

        // get station names directly from Station objects
        String startName = stationName(trip.getStartStation());
        String endName = (arrivalStation == null || arrivalStation.isBlank())
                ? stationName(trip.getEndStation())
                : arrivalStation;

        return new Billing(
                trip.getTripId(),
                userId,
                trip.getBikeId(),
                startName,
                endName,
                start,
                endTime,
                (int) minutes,
                amountCents
        );
    }

    // compute final charge and save
    public Billing calculateAndStore(String userId, Trip trip, boolean operatorActingAsRider) {
        Date start = trip.getStartTime();
        Date end = (trip.getEndTime() != null) ? trip.getEndTime() : new Date();

        long durationMs = Math.max(0, end.getTime() - start.getTime());
        long minutes = Math.max(1, (long) Math.ceil(durationMs / 60000.0));
        int baseAmountCents = Billing.BASE_FEE_CENTS
                + (int) (minutes * Billing.PER_MINUTE_FEE_CENTS);

        //discount if operator is acting as rider
        double factor = 1.0;
        if (operatorActingAsRider) {
            factor *= 0.90; // operators get a 10% discount
        }

        int amountCents = (int) Math.round(baseAmountCents * factor);

        String startName = stationName(trip.getStartStation());
        String endName = stationName(trip.getEndStation());

        Billing b = new Billing(
                trip.getTripId(),
                userId,
                trip.getBikeId(),
                startName,
                endName,
                start,
                end,
                (int) minutes,
                amountCents
        );

        b.setPaymentId("pay_" + UUID.randomUUID());

        byTrip.put(trip.getTripId(), b);
        byUser.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>())).add(b);

        return b;
    }

    public Billing calculateAndStore(String userId, Trip trip) {
        return calculateAndStore(userId, trip, false);
    }

    public Billing getByTripId(String tripId) {
        return byTrip.get(tripId);
    }

    // list all past bills for a user
    public List<Billing> listForUser(String userId) {
        List<Billing> list = byUser.get(userId);
        return (list == null) ? List.of() : List.copyOf(list);
    }
}
