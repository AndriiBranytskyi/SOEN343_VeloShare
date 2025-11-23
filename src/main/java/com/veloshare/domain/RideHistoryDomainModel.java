package com.veloshare.domain;
import static java.util.Map.entry;
import java.util.*;

public class RideHistoryDomainModel {
    private final Map<String, Trip> completed = new HashMap<>();

    private final List<Trip> trips=new ArrayList<>();
    private final List<ReservationEvent> claimedReservations = new ArrayList<>();
    private final List<ReservationEvent> missedReservations = new ArrayList<>();

    private static class ReservationEvent {
    String userId;
    String reservationId;
    Date when;
    boolean claimed;

    ReservationEvent(String userId, String reservationId, Date when, boolean claimed) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.when = when;
        this.claimed = claimed;
    }
}

    public void recordCompleted(Trip trip) {
        if (trip != null && trip.getTripId() != null && trip.getEndTime() != null) {
            completed.put(trip.getTripId(), trip);
            trips.add(trip);  // <-- IMPORTANT: add to list used by countTrips
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

        double finalCost = t.getCost();          
        double baseCost  = t.getBaseCost();      
        double flexUsed  = t.getFlexUsed(); 


    Map<String, Object> details = Map.ofEntries(
        entry("tripId", t.getTripId()),
        entry("rider", t.getUserId()),
        entry("startStation", t.getStartStation()!=null ? t.getStartStation().getName() : "-"),
        entry("endStation", t.getEndStation()!=null ? t.getEndStation().getName() : "-"),
        entry("startTime", t.getStartTime()),
        entry("endTime", t.getEndTime()),
        entry("durationMinutes", minutes),
        entry("bikeType", type),
        entry("billing", finalCost),
        entry("billingFinal", finalCost),
        entry("billingBase", baseCost),
        entry("billingFlexUsed", flexUsed)
    );

    System.out.println("DETAILS = " + details);

    return details;
        }

        public void recordClaimedReservation(String userId, String reservationId, Date when) {
            claimedReservations.add(new ReservationEvent(userId, reservationId, when, true));
        }
        
        public void recordMissedReservation(String userId, String reservationId, Date when) {
            missedReservations.add(new ReservationEvent(userId, reservationId, when, false));
        }
        
        public int countTrips(String userId, Date from, Date to) {
            return (int) trips.stream()
                    .filter(t -> t.getUserId().equals(userId))
                    .filter(t -> {
                        Date s = t.getStartTime();
                        return s != null && !s.before(from) && !s.after(to);
                    })
                    .count();
        }
        
        public int countClaimedReservations(String userId, Date from, Date to) {
            return (int) claimedReservations.stream()
                    .filter(e -> e.userId.equals(userId))
                    .filter(e -> !e.when.before(from) && !e.when.after(to))
                    .count();
        }
        
        public int countMissedReservations(String userId, Date from, Date to) {
            return (int) missedReservations.stream()
                    .filter(e -> e.userId.equals(userId))
                    .filter(e -> !e.when.before(from) && !e.when.after(to))
                    .count();
        }

}
