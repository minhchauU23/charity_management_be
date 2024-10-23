package dev.ptit.charitymanagement.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {


    @Bean
    FirebaseOptions firebaseOptions() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/charity-management-b88b2-firebase-adminsdk-ktjqp-53f5015c95.json");
        return  new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();
    }

    @Bean
    FirebaseApp firebaseApp(FirebaseOptions firebaseOptions){
        return FirebaseApp.initializeApp(firebaseOptions);
    }

    @Bean
    FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
