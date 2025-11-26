package com.veloshare.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RideHistoryAdapter implements RideHistoryClient {
    private final bmsService bms;

    public RideHistoryAdapter(bmsService bms) { this.bms = bms; }

    @Override
    public Object searchByTripId(String tripId, User user) {
        return bms.getRideHistory().searchByTripId(tripId, user);
    }

    @Override
    public List<?> filter(User user, Date start, Date end, String bikeType) {
        return bms.getRideHistory().filter(user, start, end, bikeType);
    }

    @Override
    public Map<String,Object> getDetails(String tripId, User user) {
        return bms.getRideHistory().getDetails(tripId, user);
    }

    // loyalty helper methods

    @Override
    public int countTrips(String userId, Date from, Date to) {
        return bms.getRideHistory().countTrips(userId, from, to);
    }

    @Override
    public int countClaimedReservations(String userId, Date from, Date to) {
        return bms.getRideHistory().countClaimedReservations(userId, from, to);
    }

    @Override
    public int countMissedReservations(String userId, Date from, Date to) {
        return bms.getRideHistory().countMissedReservations(userId, from, to);
    }
}