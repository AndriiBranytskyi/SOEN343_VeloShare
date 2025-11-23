package com.veloshare.application.usecases;

import java.util.Calendar;
import java.util.Date;

import com.veloshare.domain.*;

public class LoyaltyService {

    private final RideHistoryClient history;

    public LoyaltyService(RideHistoryClient history) {
        this.history = history;
    }

    public LoyaltyTier computeTier(User user) {
        if (user == null || user.getRole() != Role.RIDER) {
            return LoyaltyTier.NONE;
        }

        if (!satisfiesBronze(user)) return LoyaltyTier.NONE;
        if (!satisfiesSilver(user)) return LoyaltyTier.BRONZE;
        if (!satisfiesGold(user))   return LoyaltyTier.SILVER;
        return LoyaltyTier.GOLD;
    }

    // bronze

    private boolean satisfiesBronze(User user) {
        String userId = user.getUserId();
        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.YEAR, -1);
        Date oneYearAgo = cal.getTime();

        int missed = historyCountMissedReservations(userId, oneYearAgo, now);
        if (missed > 0) return false; 

        

        int tripsLastYear = historyCountTrips(userId, oneYearAgo, now);
        if (tripsLastYear <= 10) return false; 

        return true;
    }

    // silver

    private boolean satisfiesSilver(User user) {
        if (!satisfiesBronze(user)) return false;

        String userId = user.getUserId();
        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.YEAR, -1);
        Date oneYearAgo = cal.getTime();

        int claimed = historyCountClaimedReservations(userId, oneYearAgo, now);
        if (claimed < 5) return false; 

        // SL-003: >5 trips per month for last 3 months
        Calendar monthCal = Calendar.getInstance();
        monthCal.setTime(now);

        for (int i = 0; i < 3; i++) {
            // end of month = current monthCal time
            Date monthEnd = monthCal.getTime();

            // start of month:
            monthCal.set(Calendar.DAY_OF_MONTH, 1);
            Date monthStart = monthCal.getTime();

            int trips = historyCountTrips(userId, monthStart, monthEnd);
            if (trips <= 5) {
                return false;
            }

            // go to previous month
            monthCal.add(Calendar.MONTH, -1);
            monthCal.set(Calendar.DAY_OF_MONTH, monthCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        }

        return true;
    }

    // gold

    private boolean satisfiesGold(User user) {
        if (!satisfiesSilver(user)) return false;

        String userId = user.getUserId();
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -3);
        Date threeMonthsAgo = cal.getTime();

        Date weekStart = threeMonthsAgo;
        while (weekStart.before(now)) {
            cal.setTime(weekStart);
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            Date weekEnd = cal.getTime().before(now) ? cal.getTime() : now;

            int trips = historyCountTrips(userId, weekStart, weekEnd);
            if (trips <= 5) {
                return false; 
            }

            weekStart = weekEnd;
        }
        return true;
    }

    // helpers

    private int historyCountTrips(String userId, Date from, Date to) {
        
        return history.countTrips(userId, from, to); 
    }

    private int historyCountClaimedReservations(String userId, Date from, Date to) {
        return history.countClaimedReservations(userId, from, to);
    }

    private int historyCountMissedReservations(String userId, Date from, Date to) {
        return history.countMissedReservations(userId, from, to);
    }
}