package com.veloshare.api;

import com.veloshare.application.usecases.BillingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final BillingService billingService;

    public AccountController(BillingService billingService) {
        this.billingService = billingService;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getAccount(@PathVariable String userId) {

        double flexDollars = billingService.getFlexDollarsForUser(userId);

        // loyalty is not implemented yet -> always Entry
        String loyaltyTier = "Entry";

        // TODO: later pull real roles from RolesRepo / auth
        List<String> roles = List.of("rider");  // stub: single role
        String activeRole = "rider";

        return Map.of(
                "userId", userId,
                "loyaltyTier", loyaltyTier,
                "flexDollars", flexDollars,
                "roles", roles,
                "activeRole", activeRole
        );
    }
}
