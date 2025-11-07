package com.veloshare.domain;

import java.util.*;

public class RideHistoryDomainModel {
    private final Map<String, Trip> completed = new HashMap<>();

    public void recordCompleted(Trip trip) {
        if (trip != null && trip.getTripId() != null && trip.getEndTime() != null) {
            completed.put(trip.getTripId(), trip);
        }
    }

    public Trip searchByTripId(String tripId, User user) {
        Trip t = completed.get(tripId);
        if (t == null) return null;
        if (user.getRole() == Role.OPERATOR || user.getUserId().equals(t.getUserId())) {
            return t;
        }
        return null;
    }

    public List<Trip> filter(User user, Date start, Date end, String bikeType) {
        return completed.values().stream()
                .filter(t -> user.getRole() == Role.OPERATOR || user.getUserId().equals(t.getUserId()))
                .filter(t -> {
                    Date et = t.getEndTime();
                    if (et == null) return false;
                    if (start != null && et.before(start)) return false;
                    if (end != null && et.after(end)) return false;
                    if (bikeType != null && !bikeType.isBlank()) {
                        String type = t.getBike() != null ? t.getBike().getType() : "standard";
                        if (!bikeType.equalsIgnoreCase(type)) return false;
                    }
                    return true;
                })
                .sorted(Comparator.comparing(Trip::getEndTime).reversed())
                .toList();
    }

    public Map<String,Object> getDetails(String tripId, User user) {
        Trip t = searchByTripId(tripId, user);
        if (t == null) return null;
        long minutes = Math.max(1, (long)Math.ceil(t.getDurationMillis()/60000.0));
        String type = t.getBike()!=null ? t.getBike().getType() : "standard";
        return Map.of(
            "tripId", t.getTripId(),
            "rider", t.getUserId(),
            "startStation", t.getStartStation()!=null?t.getStartStation().getName():"-",
            "endStation", t.getEndStation()!=null?t.getEndStation().getName():"-",
            "startTime", t.getStartTime(),
            "endTime", t.getEndTime(),
            "durationMinutes", minutes,
            "bikeType", type,
            "billing", t.getCost()
        );
    }
}
