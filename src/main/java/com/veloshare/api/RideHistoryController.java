package com.veloshare.api;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.usecases.RideHistoryService;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/history")
public class RideHistoryController {

    private final RideHistoryService service;
    private final CurrentUserProvider current;

    public RideHistoryController(RideHistoryService service, CurrentUserProvider current) {
        this.service = service;
        this.current = current;
    }

    @GetMapping("/search")
    public Object search(@RequestParam String tripId, HttpServletRequest req) {
        User u = current.requireUser(req);
        return service.search(tripId, u);
    }

    @GetMapping("/filter")
    public List<?> filter(
            @RequestParam(required = false) String start,
            @RequestParam(required = false) String end,
            @RequestParam(required = false) String bikeType,
            HttpServletRequest req) {

        User u = current.requireUser(req);

        Date startDate = null;
        Date endDate = null;

        if (start != null && !start.isBlank()) {
            try {
                startDate = java.sql.Date.valueOf(start);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid start date format, expected YYYY-MM-DD");
            }
        }

        if (end != null && !end.isBlank()) {
            try {
                endDate = java.sql.Date.valueOf(end);
            } catch (IllegalArgumentException e) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Invalid end date format, expected YYYY-MM-DD");
            }
        }

        String actAs = req.getHeader("X-Act-As");
        boolean operatorActingAsRider
                = u.getRole() == Role.OPERATOR
                && "RIDER".equalsIgnoreCase(actAs); //if user is an operator acting as a rider

        boolean ownOnly = (u.getRole() == Role.RIDER) || operatorActingAsRider;

        if (startDate == null && endDate == null && (bikeType == null || bikeType.isBlank())) {
            return service.filter(u, null, null, null, ownOnly);
        }

        return service.filter(u, startDate, endDate, bikeType, ownOnly); //if false default filtering will then be used
    }

    @GetMapping("/{tripId}")
    public Map<String, Object> details(@PathVariable String tripId, HttpServletRequest req) {
        User u = current.requireUser(req);
        return service.details(tripId, u);
    }
}
