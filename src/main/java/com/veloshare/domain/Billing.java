package com.veloshare.domain;

import java.util.Date;
public class Billing {
    // pricing 
    public static final int BASE_FEE_CENTS=200;
    public static final int PER_MINUTE_FEE_CENTS=10;

    // identity
    private final String tripId;
    public final String userId;

    // trip facts
    private final String bikeId;
    private final String originStation;
    private final String arrivalStation;
    private final Date startTime;
    private final Date endTime;

    // charges
    private final int minutesBilled;
    private final int amountCents;

    // flex details
    private int baseAmountCents;
    private int flexUsedCents;
    
    // payment details
    private String paymentId;


    public Billing(
            String tripId,
            String userId,
            String bikeId,
            String originStation,
            String arrivalStation,
            Date startTime,
            Date endTime,
            int minutesBilled,
            int amountCents
    ) {
        this.tripId = tripId;
        this.userId = userId;
        this.bikeId = bikeId;
        this.originStation = originStation;
        this.arrivalStation = arrivalStation;
        this.startTime = startTime == null ? null : new Date(startTime.getTime());
        this.endTime = endTime == null ? null : new Date(endTime.getTime());
        this.minutesBilled = minutesBilled;
        this.amountCents = amountCents;
    }


    public String getTripId() {
        return tripId;
    }
    public String getUserId() {
        return userId;
    }

    public String getBikeId() {
        return bikeId;
    }
    public String getOriginStation() {
        return originStation;
    }
    public String getArrivalStation() {
        return arrivalStation;
    }
    public Date getStartTime() {
        return startTime == null ? null : new Date(startTime.getTime());
    }
    public Date getEndTime() {
        return endTime == null ? null : new Date(endTime.getTime());
    }
    public int getMinutesBilled() {
        return minutesBilled;
    }
    public int getAmountCents() {
        return amountCents;
    }
    public String getPaymentId() {
        return paymentId;
    }
    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
    public int getBaseAmountCents() {
        return baseAmountCents;
    }

    public void setBaseAmountCents(int baseAmountCents) {
        this.baseAmountCents = baseAmountCents;
    }

    public int getFlexUsedCents() {
        return flexUsedCents;
    }

    public void setFlexUsedCents(int flexUsedCents) {
        this.flexUsedCents = flexUsedCents;
    }
}

