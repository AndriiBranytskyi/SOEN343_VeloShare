package com.veloshare.config;

import java.io.FileInputStream;

import org.springframework.context.annotation.Configuration;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

@Configuration
public class FirebaseAdminConfig {

    public FirebaseAdminConfig() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
            	FileInputStream serviceAccount = new FileInputStream(
                        "src/main/java/com/veloshare/config/firebaseAdminConfigCredentials.json"
                    );

                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                FirebaseApp.initializeApp(options);
                System.out.println("Firebase Admin initialized");
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize Firebase Admin", e);
            }
        }
    }
}
