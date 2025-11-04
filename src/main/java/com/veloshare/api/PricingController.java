// public pricing (no login)

package com.veloshare.api;


import org.springframework.web.bind.annotation.*;
import com.veloshare.application.billing.BillingService;
import com.veloshare.domain.pricing.PricePerMinutePolicy;
import com.veloshare.domain.pricing.PricingPolicy;

@RestController
@RequestMapping("/api")
public class PricingController {
    private final BillingService billing;

    public PricingController(BillingService billing){
        this.billing=billing;
    }

    record PricingDto(double baseFee, double pricePerMinutes){}

    @GetMapping
    public PricingDto pricing(){
        PricingPolicy p=billing.getPricing();
        if (p instanceof PricePerMinutePolicy ppm){
            return new PricingDto(ppm.getBaseFee(),ppm.getPricePerMinute());
        }

        // fallback if policy chnages
        return new PricingDto(2.00,0.10);
    }
    
}
