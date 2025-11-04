package com.veloshare.domain.pricing;
import com.veloshare.domain.Trip;

public class PricePerMinutePolicy implements PricingPolicy{
    
    private final double baseFee;       //$2.00
    private final double pricePerMinute;    //$0.10

    public PricePerMinutePolicy(double baseFee,double pricePerMinute){
        this.baseFee = baseFee;
        this.pricePerMinute = pricePerMinute;
    }

    @Override
    public double compute(Trip trip) {
        // trip in ms 
        long ms=trip.getEndTime().getTime()-trip.getStartTime().getTime();
        // trip in min
        double minutes=Math.max(0.0,ms/60000.0);
        // cost of trip
        double amount=baseFee+minutes*pricePerMinute;

        return amount;
    }

    // getters
    public double getBaseFee() {
        return baseFee;
    }

    public double getPricePerMinute() {
        return pricePerMinute;
    }
}
