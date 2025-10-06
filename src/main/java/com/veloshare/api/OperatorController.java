package com.veloshare.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.application.dto.MoveBikeCmd;
import com.veloshare.application.usecases.OperatorService;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

@RestController
@RequestMapping("/api/ops")
public class OperatorController {

    private final OperatorService ops;

    public OperatorController(OperatorService ops) {
        this.ops = ops;
    }

    @PostMapping("/move-bike")
    public ResponseEntity<?> move(@RequestBody MoveBikeReq req) {
        var user = new User(req.operatorId(), req.operatorName(), Role.OPERATOR);
        var r = ops.moveBike(new MoveBikeCmd(req.bikeId(), req.fromStation(), req.toStation()), user);
        return r.isOk() ? ResponseEntity.ok().build() : ResponseEntity.badRequest().body(r.getError());
    }
}
