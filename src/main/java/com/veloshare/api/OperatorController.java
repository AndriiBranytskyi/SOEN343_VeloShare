package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.dto.MoveBikeCmd;
import com.veloshare.application.usecases.OperatorService;
import com.veloshare.domain.User;
import com.veloshare.domain.bmsService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/ops")
public class OperatorController {

    private final OperatorService ops;
    private final CurrentUserProvider current;
    private final bmsService bms;

    public OperatorController(OperatorService ops, CurrentUserProvider current, bmsService bms) {
        this.ops = ops;
        this.current = current;
        this.bms = bms;
    }

    @PostMapping("/move-bike")
    public ResponseEntity<?> move(@RequestBody MoveBikeReq req, HttpServletRequest http) {
        User operator = current.requireOperator(http);
        var r = ops.moveBike(new MoveBikeCmd(req.bikeId(), req.fromStation(), req.toStation()), operator);
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

    @PostMapping("/stations/{name}/oos")
    public ResponseEntity<?> stationOOS(@PathVariable String name, HttpServletRequest http) {
        User operator = current.requireOperator(http);
        var r = ops.setStationOutOfService(name, true, operator);
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

    @PostMapping("/stations/{name}/active")
    public ResponseEntity<?> stationActive(@PathVariable String name, HttpServletRequest http) {
        User operator = current.requireOperator(http);
        var r = ops.setStationOutOfService(name, false, operator);
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

    @PostMapping("/bikes/{bikeId}/maintenance")
    public ResponseEntity<?> bikeMaintenance(@PathVariable String bikeId, HttpServletRequest http) {
        User operator = current.requireOperator(http);
        var r = ops.setBikeMaintenance(bikeId, true, operator);
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

    @PostMapping("/bikes/{bikeId}/available")
    public ResponseEntity<?> bikeAvailable(@PathVariable String bikeId, HttpServletRequest http) {
        User operator = current.requireOperator(http);
        var r = ops.setBikeMaintenance(bikeId, false, operator);
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }

    @PostMapping("/reset")
    public String resetSystem() {
        bms.resetToInitial();
        return "System reset to initial config.";
    }
}
