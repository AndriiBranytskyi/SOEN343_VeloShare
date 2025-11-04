package com.veloshare.application.billing;

import com.veloshare.domain.billing.BillingRecord;

public interface PaymentGateway {
    void charge(String userId,double amount,BillingRecord record) throws Exception;
    
}
