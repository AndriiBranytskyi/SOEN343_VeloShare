package com.veloshare.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.veloshare.api.security.CurrentUserProvider;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
public class ProfileController {

    private final CurrentUserProvider current;

    public ProfileController(CurrentUserProvider current) {
        this.current = current;
    }

    record ProfileDto(String uid, String name, String role, boolean canRide, boolean canOperate) {

    }

    @GetMapping("/profile")
    public ProfileDto profile(HttpServletRequest req) {
        User u = current.requireUser(req);
        Role role = u.getRole();
        boolean canOperate = (role == Role.OPERATOR);
        boolean canRide = true; //true cz rider and operator can ride bikes
        return new ProfileDto(u.getUserId(), u.getName(), u.getRole().name(), canRide, canOperate);
    }
}
