package com.veloshare.application.usecases;

import com.veloshare.domain.User;
import com.veloshare.domain.RideHistoryClient;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class RideHistoryService {
    private final RideHistoryClient client;
    public RideHistoryService(RideHistoryClient client) { this.client = client; }

    public Object search(String tripId, User user) { return client.searchByTripId(tripId, user); }
    public List<?> filter(User user, Date start, Date end, String bikeType) { return client.filter(user, start, end, bikeType); }
    public Map<String,Object> details(String tripId, User user) { return client.getDetails(tripId, user); }
}
