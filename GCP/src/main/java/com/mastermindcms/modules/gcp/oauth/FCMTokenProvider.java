package com.mastermindcms.modules.gcp.oauth;

import com.mastermindcms.modules.gcp.config.GCPConfiguration;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * FCMTokenProvider is responsible for initializing Firebase App with service account credentials
 * and providing OAuth 2.0 access tokens for authenticating requests to Firebase Cloud Messaging (FCM)
 * via the HTTP v1 API.
 * <p>
 * This component loads the service account credentials and scope from the {@link GCPConfiguration}
 * and can be used to programmatically obtain short-lived access tokens to authorize push notification
 * requests to FCM.
 * </p>
 *
 * <p><b>Example usage:</b></p>
 * <pre>{@code
 * String accessToken = fcmTokenProvider.getAuthToken();
 * }</pre>
 *
 * @author mastermind
 */
@Slf4j
@Service
public class FCMTokenProvider {

    /**
     * Injected configuration that provides the service account key path and FCM scope URL.
     */
    @Autowired
    private GCPConfiguration gcpConfiguration;

    /**
     * Initializes the FirebaseApp instance using credentials from the service account file.
     * This method is called automatically after the bean is constructed.
     * <p>
     * It validates that the service account path and scope URL are set,
     * and logs an error if the initialization fails.
     * </p>
     */
    @PostConstruct
    public void init() {
        if (Objects.nonNull(gcpConfiguration.getFcmServiceAccountKey()) && !gcpConfiguration.getFcmServiceAccountKey().isEmpty()
                && Objects.nonNull(gcpConfiguration.getFcmScopeUrl()) && !gcpConfiguration.getFcmScopeUrl().isEmpty()) {
            try {
                FileInputStream serviceAccount = new FileInputStream(gcpConfiguration.getFcmServiceAccountKey());
                FirebaseOptions options = FirebaseOptions.builder()
                        .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                        .build();
                FirebaseApp.initializeApp(options);
            } catch (IOException e) {
                log.error("FCM initialize:", e);
            }
        }
    }

    /**
     * Obtains a fresh OAuth 2.0 access token scoped for Firebase Cloud Messaging from the service account.
     *
     * @return a valid bearer token string used to authorize FCM HTTP v1 requests
     * @throws IOException if reading the service account file fails or token cannot be fetched
     * @throws IllegalStateException if required configuration (service account path or scope) is missing
     */
    public String getAuthToken() throws IOException {
        if (gcpConfiguration.getFcmServiceAccountKey() == null
                || gcpConfiguration.getFcmServiceAccountKey().isEmpty()
                || gcpConfiguration.getFcmScopeUrl() == null
                || gcpConfiguration.getFcmScopeUrl().isEmpty()) {
            throw new IllegalStateException("Service account key path or scope url is not configured.");
        }

        GoogleCredentials credentials = GoogleCredentials
                .fromStream(new FileInputStream(gcpConfiguration.getFcmServiceAccountKey()))
                .createScoped(gcpConfiguration.getFcmScopeUrl());

        credentials.refreshIfExpired();

        return credentials.getAccessToken().getTokenValue();
    }
}
