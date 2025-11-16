package com.veloshare.auth;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veloshare.domain.Role;

public class RolesRepo {

    private final Map<String, Role> roles = new ConcurrentHashMap<>(); //thread-safe and designed for concurrent access from multiple threads.

    public RolesRepo() {
        loadRolesFromFile("./roles.json"); // path relative to project root
    }

    private void loadRolesFromFile(String path) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File(path);

            if (!file.exists()) {
                System.out.println("⚠️ roles.json not found at " + path + ", using defaults");
                return;
            }

            Map<String, List<String>> json = mapper.readValue(file, Map.class);
            List<String> operators = json.get("operators");
            if (operators != null) {
                for (String uid : operators) {
                    roles.put(uid, Role.OPERATOR);
                }
            }

            System.out.println("Loaded operators from roles.json: " + roles.keySet());
        } catch (IOException e) {
            System.err.println("Failed to load roles.json: " + e.getMessage());
        }
    }

    public Role getRole(String uid) {
        return roles.getOrDefault(uid, Role.RIDER);
    }

    public void setRole(String uid, Role role) {
        roles.put(uid, role);
    }
}
