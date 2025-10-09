package com.veloshare.api.security;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.veloshare.auth.RolesRepo;
import com.veloshare.domain.Role;
import com.veloshare.domain.User;

import jakarta.servlet.http.HttpServletRequest;

//turn the Firebase idToken sent by the browser into a domain User.
@Component
public class CurrentUserProvider {

    private final RolesRepo roles;

    public CurrentUserProvider(RolesRepo roles) {
        this.roles = roles;
    }

    public User requireUser(HttpServletRequest request) {
        String token = extractBearer(request); //extract authorization from req
        FirebaseToken decoded = verify(token);
        // Map Firebase user to domain User
        String uid = decoded.getUid();
        String name = decoded.getName() != null ? decoded.getName()
                : (decoded.getEmail() != null ? decoded.getEmail() : uid);
        Role role = roles.getRole(uid);
        return new User(uid, name, role);
    }

    public User requireOperator(HttpServletRequest request) {
        User u = requireUser(request);
        if (u.getRole() != Role.OPERATOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operator role required");
        }
        return u;
    }

    //Bearer is a keyword in the Authorization header to indicate that the client is sending a token 
    private String extractBearer(HttpServletRequest req) {
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.toLowerCase().startsWith("bearer ")) {
            return auth.substring(7).trim(); //cut off bearer
        }
        //X-Auth-Token in case some systems use this header instead of authorization
        String fallback = req.getHeader("X-Auth-Token");
        if (fallback != null && !fallback.isBlank()) {
            return fallback.trim();
        }
        throw new IllegalArgumentException("Missing Authorization: Bearer <token>");
    }

    private FirebaseToken verify(String idToken) {
        try {
            return FirebaseAuth.getInstance().verifyIdToken(idToken, true);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid/expired token");
        }
    }
}
