package com.veloshare.application.billing;

import com.veloshare.domain.billing.BillingRecord;

public class MockPaymentGateway implements PaymentGateway {
    @Override
    public void charge(String userId,double amount, BillingRecord record){
        System.out.println("MockPaymentGateway: Charging user "+userId+" amount $"+amount+" for trip "+record.getTripId());
    }


    
}
