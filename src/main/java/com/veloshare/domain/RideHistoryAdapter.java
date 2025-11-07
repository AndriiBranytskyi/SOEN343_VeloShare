package com.veloshare.domain;

import java.util.*;

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
}
