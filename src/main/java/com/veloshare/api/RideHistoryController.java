package com.veloshare.api;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.application.usecases.RideHistoryService;
import com.veloshare.domain.User;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

        // No params → return all rides
        if (startDate == null && endDate == null && (bikeType == null || bikeType.isBlank())) {
            return service.filter(u, null, null, null);
        }

        return service.filter(u, startDate, endDate, bikeType);
    }

    @GetMapping("/{tripId}")
    public Map<String,Object> details(@PathVariable String tripId, HttpServletRequest req) {
        User u = current.requireUser(req);
        return service.details(tripId, u);
    }
}
