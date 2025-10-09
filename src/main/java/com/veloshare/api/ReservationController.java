package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.dto.ReserveBikeCmd;
import com.veloshare.application.usecases.ReservationService;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservations;
    private final CurrentUserProvider current;

    public ReservationController(ReservationService reservations, CurrentUserProvider current) {
        this.reservations = reservations;
        this.current = current;
    }

    @PostMapping
    public ResponseEntity<?> reserve(@RequestBody ReserveBikeReq req) {
        var r = reservations.reserve(new ReserveBikeCmd(req.userId(), req.bikeId(), req.stationName(), req.minutes()));
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancel(@PathVariable String reservationId) {
        var r = reservations.cancel(reservationId);
        return r.isOk() ? ResponseEntity.noContent().build()
                : ResponseEntity.badRequest().body(r.getError());
    }
}
