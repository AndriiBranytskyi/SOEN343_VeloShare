package com.veloshare.config;

import java.io.FileInputStream;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;

/*@Configuration
public class FirebaseAdminConfig {

    public FirebaseAdminConfig() {
        if (FirebaseApp.getApps().isEmpty()) {
            try {
                // Get path from environment variable
                String credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                if (credPath == null || credPath.isBlank()) {
                    throw new RuntimeException(
                        "GOOGLE_APPLICATION_CREDENTIALS env var not set!");
                }

                try (FileInputStream serviceAccount = new FileInputStream(credPath)) {
                    FirebaseOptions options = FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                            .build();
                    FirebaseApp.initializeApp(options);
                    System.out.println("Firebase Admin initialized");
                }
            } catch (Exception e) {
                throw new RuntimeException("Failed to initialize Firebase Admin", e);
            }
        }
    }
}
*/

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