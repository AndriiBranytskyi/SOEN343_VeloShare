package com.veloshare.application.billing;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.veloshare.domain.Trip;
import com.veloshare.domain.billing.BillingRecord;
import com.veloshare.domain.pricing.PricingPolicy;

public class BillingService {

    private final PricingPolicy pricing;
    private final PaymentGateway gateway;

    // userId-->connect to db
    private final Map<String, List<BillingRecord>> billingRecords = new ConcurrentHashMap<>();

    public BillingService(PricingPolicy pricingPolicy, PaymentGateway paymentGateway) {
        this.pricing = pricingPolicy;
        this.gateway = paymentGateway;
    }

    public BillingRecord bill(Trip trip) throws Exception{
        double amount=pricing.compute(trip);
        BillingRecord record=new BillingRecord(
            trip.getTripId(),
            trip.getUserId(),
            trip.getBikeId(),
            trip.getStartStation().getName(),
            trip.getEndStation().getName(),
            trip.getStartTime(),
            trip.getEndTime(),
            amount
        );
        // store in memory
        billingRecords.computeIfAbsent(trip.getUserId(), k -> Collections.synchronizedList(new ArrayList<>()))
            .add(record);
        // charge via gateway(mock/real)
        gateway.charge(trip.getUserId(), amount, record);
        return record;
    }

    public List<BillingRecord> historyFor(String userId) {
        // return an immutable snapshot of the user's billing history to avoid mutation
        return Collections.unmodifiableList(billingRecords.getOrDefault(userId, List.of()));
    }

    public PricingPolicy getPricing() { return pricing; }

    
}
