package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.application.dto.EndTripCmd;
import com.veloshare.application.dto.StartTripCmd;
import com.veloshare.application.usecases.TripService;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    public final TripService trips;

    public TripController(TripService trips) {
        this.trips = trips;
    }

    @PostMapping("/start")
    public ResponseEntity<?> start(@RequestBody StartTripReq req) {
        var user = new User(req.userId(), req.userName(), Role.valueOf(req.role()));
        var r = trips.startTrip(new StartTripCmd(req.userId(), req.bikeId(), req.stationName(), req.estimatedCost(), req.estimatedDistance()), user);
        return r.isOk() ? ResponseEntity.ok().build()
                : ResponseEntity.badRequest().body(r.getError());
    }

    @PostMapping("/end")
    public ResponseEntity<?> end(@RequestBody EndTripCmd req) {
        var r = trips.endTrip(new EndTripCmd(req.tripId(), req.stationName()));
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

}
