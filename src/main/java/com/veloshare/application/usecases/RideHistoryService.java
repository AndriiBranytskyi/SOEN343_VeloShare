package com.veloshare.application.usecases;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.veloshare.domain.RideHistoryClient;
import com.veloshare.domain.Trip;
import com.veloshare.domain.User;

public class RideHistoryService {

    private final RideHistoryClient client;

    public RideHistoryService(RideHistoryClient client) {
        this.client = client;
    }

    public Object search(String tripId, User user) {
        return client.searchByTripId(tripId, user);
    }

    public List<?> filter(User user, Date start, Date end, String bikeType) {
        return client.filter(user, start, end, bikeType);
    }

    public Map<String, Object> details(String tripId, User user) {
        return client.getDetails(tripId, user);
    }

    //for trip history when signed in as an operator and wants to view only their trip history
    public List<?> filter(User user,
            Date start,
            Date end,
            String bikeType,
            boolean ownOnly) {

        //same as old filtering        
        List<?> raw = client.filter(user, start, end, bikeType);
        if (!ownOnly || raw == null || raw.isEmpty() || user == null) {
            return raw;
        }

        String uid = user.getUserId();
        return raw.stream()
                .filter(o -> (o instanceof Trip t) && uid.equals(t.getUserId()))
                .toList();
    }
}
