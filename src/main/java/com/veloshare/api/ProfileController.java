package com.veloshare.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.usecases.LoyaltyService;
import com.veloshare.domain.LoyaltyTier;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final CurrentUserProvider current;
    private final LoyaltyService loyalty;

    public ProfileController(CurrentUserProvider current, LoyaltyService loyalty) {
        this.current = current;
        this.loyalty = loyalty;
    }

    record ProfileDto(String uid, String name, String role, boolean canRide, boolean canOperate) {

    }

    @GetMapping("/profile")
public ProfileDto profile(HttpServletRequest req) {
    User u = current.requireUser(req);
    Role role = u.getRole();
    boolean canOperate = (role == Role.OPERATOR);
    boolean canRide = true;

    LoyaltyTier oldTier = u.getTier();
    LoyaltyTier newTier = loyalty.computeTier(u);
    u.setTier(newTier);

    String status;
    if (oldTier == null || oldTier == LoyaltyTier.NONE) {
        status = (newTier != LoyaltyTier.NONE) ? "UPGRADED" : "UNCHANGED";
    } else if (newTier.ordinal() > oldTier.ordinal()) {
        status = "UPGRADED";
    } else if (newTier.ordinal() < oldTier.ordinal()) {
        status = "DOWNGRADED";
    } else {
        status = "UNCHANGED";
    }

    return new ProfileDto(
            u.getUserId(),
            u.getName(),
            u.getRole().name(),
            canRide,
            canOperate
    );
}
}
