package com.veloshare.api;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.*;
import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.billing.BillingService;
import com.veloshare.domain.User;
import com.veloshare.domain.billing.BillingRecord;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    private final BillingService billing;
    private final CurrentUserProvider current;

    public BillingController(BillingService billing, CurrentUserProvider current){
        this.billing=billing;
        this.current=current;
    }
    record BillingHistoryItem(
        String tripId,
        String bikeId,
        String originStation,
        String destinationStation,
        java.util.Date startTime,
        java.util.Date endTime,
        double amount
    ) {}

    @GetMapping("/history")
    public List<BillingHistoryItem> history (HttpServletRequest http){
        User u=current.requireUser(http);
        List<BillingRecord> recs=billing.historyFor(u.getUserId());
        return recs.stream().map(r->new BillingHistoryItem(
            r.getTripId(),
            r.getBikeId(),
            r.getOriginStation(),
            r.getDestinationStation(),
            r.getStartTime(),
            r.getEndTime(),
            r.getAmount()
        )).collect(Collectors.toList());
    }
    
}
