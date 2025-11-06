package com.veloshare.application.usecases;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.veloshare.domain.Billing;
import com.veloshare.domain.Trip;

public class BillingService {

    // userId -> bills 
    private final Map<String, List<Billing>> byUser = new ConcurrentHashMap<>();
    // tripId -> bill
    private final Map<String, Billing> byTrip = new ConcurrentHashMap<>();

    //Compute amount and store under both trip and user.
    public Billing calculateAndStore(String userId, Trip trip) {
        long durationMs = trip.getDurationMillis();
        long minutes = Math.max(1, (long) Math.ceil(durationMs / 60000.0));

        int amountCents = Billing.BASE_FEE_CENTS
                + (int) (minutes * Billing.PER_MINUTE_FEE_CENTS);

        Billing b = new Billing(trip.getTripId(), userId, (int) minutes, amountCents);

        byTrip.put(trip.getTripId(), b);
        byUser.computeIfAbsent(userId, k ->
                Collections.synchronizedList(new ArrayList<>())
        ).add(b);

        return b;
    }

    public Billing getByTripId(String tripId) {
        return byTrip.get(tripId);
    }

    //List all bills for a user 
    public List<Billing> listForUser(String userId) {
        List<Billing> list = byUser.get(userId);
        return (list == null) ? List.of() : List.copyOf(list);
    }
}