package com.veloshare.domain.pricing;

import com.veloshare.domain.Trip;

public interface PricingPolicy{
    double compute(Trip trip);
}

