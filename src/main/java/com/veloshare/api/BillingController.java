package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.Result;
import com.veloshare.application.dto.EndTripCmd;
import com.veloshare.application.usecases.BillingService;
import com.veloshare.application.usecases.TripService;
import com.veloshare.domain.Billing;
import com.veloshare.domain.User;                         
import jakarta.servlet.http.HttpServletRequest;           

@RestController
@RequestMapping("/api/billing")
public class BillingController {
    private final TripService trips;
    private final BillingService billing;
    private final CurrentUserProvider current;

    public BillingController(TripService trips, BillingService billing, CurrentUserProvider current) {
        this.trips = trips;
        this.billing = billing;
        this.current = current;
    }

    // End trip and bill in one shot
    @PostMapping("/trips/{tripId}/end-and-charge")
    public ResponseEntity<?> endAndCharge(
            @PathVariable String tripId,
            @RequestParam String endStation,
            HttpServletRequest http) {                    

        String uid = current.requireUser(http).getUserId();  // get UID from the authenticated user
        Result<Billing> r = trips.endTripAndBill(new EndTripCmd(tripId, endStation), uid);
        return r.isOk() ? ResponseEntity.ok(r.getValue())
                        : ResponseEntity.badRequest().body(r.getError());
    }

    @GetMapping("/trips/{tripId}")
    public ResponseEntity<?> find(@PathVariable String tripId) {
        Billing b = billing.getByTripId(tripId);
        return (b == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(b);
    }

    // for listing all bills for the current user
    @GetMapping("/mine")
    public ResponseEntity<?> mine(HttpServletRequest http) {
        var user = current.requireUser(http);
        return ResponseEntity.ok(billing.listForUser(user.getUserId()));
    }
}