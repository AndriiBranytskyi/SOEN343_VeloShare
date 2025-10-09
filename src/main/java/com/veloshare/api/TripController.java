package com.veloshare.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.dto.EndTripCmd;
import com.veloshare.application.dto.StartTripCmd;
import com.veloshare.application.usecases.TripService;
import com.veloshare.domain.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    public final TripService trips;
    private final CurrentUserProvider currentUser;

    public TripController(TripService trips, CurrentUserProvider currentUser) {
        this.trips = trips;
        this.currentUser = currentUser;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody StartTripReq req, HttpServletRequest http) {
        User user = currentUser.requireUser(http);
        var r = trips.startTrip(new StartTripCmd(user.getUserId(), req.bikeId(), req.stationName(),
                req.estimatedCost(), req.estimatedDistance()), user);
        return r.isOk()
                ? ResponseEntity.ok(Map.of("tripId", r.getValue()))
                : ResponseEntity.badRequest().body(r.getError());

    }

    @PostMapping("/end")
    public ResponseEntity<?> end(@RequestBody EndTripReq req) {
        String id = req.tripId() == null ? "" : req.tripId().trim();
        String station = req.stationName() == null ? "" : req.stationName().trim();
        System.out.println("TripController.end -> tripId='" + id + "' len=" + id.length()
                + " station='" + station + "'");

        var r = trips.endTrip(new EndTripCmd(id, station));
        return r.isOk() ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(r.getError());
    }

}
