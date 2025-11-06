package com.veloshare.domain;

public class Billing {
    public static final int BASE_FEE_CENTS=200;
    public static final int PER_MINUTE_FEE_CENTS=10;

    private final String tripId;
    public final String userId;
    private final int minutesBilled;
    private final int amountCents;


    public Billing(String tripId, String userId, int minutesBilled, int amountCents) {
        this.tripId = tripId;
        this.userId = userId;
        this.minutesBilled = minutesBilled;
        this.amountCents = amountCents;
    }

    public String getTripId() {
        return tripId;
    }
    public String getUserId() {
        return userId;
    }
    public int getMinutesBilled() {
        return minutesBilled;
    }
    public int getAmountCents() {
        return amountCents;
    }
}
