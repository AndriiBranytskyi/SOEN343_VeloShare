package com.veloshare.api;

import com.veloshare.application.usecases.BillingService;
import com.veloshare.application.usecases.LoyaltyService;
import com.veloshare.domain.LoyaltyTier;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final LoyaltyService loyalty;
    private final BillingService billingService;

    public AccountController(LoyaltyService loyalty, BillingService billingService) {
        this.loyalty = loyalty;
        this.billingService = billingService;
    }

    // DTO (optional)
    record AccountDto(
            String userId,
            String name,
            String tier,
            double flexDollars,
            List<String> roles,
            String activeRole
    ) {}

    @GetMapping("/{userId}")
    public Map<String, Object> getAccount(@PathVariable String userId) {

        User user = new User(userId, "Rider", Role.RIDER);

        LoyaltyTier tier = loyalty.computeTier(user);
        double flexDollars = billingService.getFlexDollarsForUser(userId);

        List<String> roles = List.of("RIDER");
        String activeRole = "RIDER";

        return Map.of(
                "userId", userId,
                "loyaltyTier", tier.name(),
                "flexDollars", flexDollars,
                "roles", roles,
                "activeRole", activeRole
        );
    }
}