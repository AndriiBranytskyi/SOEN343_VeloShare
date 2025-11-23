package com.veloshare.domain;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RideHistoryClient {
    Object searchByTripId(String tripId, User user);
    List<?> filter(User user, Date start, Date end, String bikeType);
    Map<String,Object> getDetails(String tripId, User user);

    // loyalty program
    int countTrips(String userId, Date from, Date to);
    int countClaimedReservations(String userId, Date from, Date to);
    int countMissedReservations(String userId, Date from, Date to);
}