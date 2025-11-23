package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.veloshare.application.dto.ReserveBikeCmd;
import com.veloshare.application.usecases.ReservationService;
import com.veloshare.application.usecases.LoyaltyService;
import com.veloshare.domain.LoyaltyTier;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservations;
    private final LoyaltyService loyalty;

    public ReservationController(ReservationService reservations,
                                 LoyaltyService loyalty) {
        this.reservations = reservations;
        this.loyalty = loyalty;
    }

    @PostMapping
    public ResponseEntity<?> reserve(@RequestBody ReserveBikeReq req) {
        User user = new User(req.userId(), "Temp", Role.RIDER);

        // Compute loyalty tier
        LoyaltyTier tier = loyalty.computeTier(user);

        int extraMinutes =
                (tier == LoyaltyTier.GOLD)   ? 5 :
                (tier == LoyaltyTier.SILVER) ? 2 :
                                               0;

        int totalMinutes = req.minutes() + extraMinutes;

        var r = reservations.reserve(
                new ReserveBikeCmd(req.userId(), req.bikeId(), req.stationName(), totalMinutes)
        );

        return r.isOk()
                ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(r.getError());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancel(@PathVariable String reservationId) {
        var r = reservations.cancel(reservationId);
        return r.isOk()
                ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().body(r.getError());
    }
}