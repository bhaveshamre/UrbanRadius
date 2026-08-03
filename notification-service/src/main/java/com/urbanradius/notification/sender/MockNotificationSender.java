package com.urbanradius.notification.sender;

import com.urbanradius.notification.client.UserServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class MockNotificationSender {

    private static final Logger log = LoggerFactory.getLogger(MockNotificationSender.class);

    public void sendEmail(UserServiceClient.UserProfileView recipient, String message) {
        log.info(
                "NOTIFICATION [EMAIL] to {} ({}) phone={}: {}",
                recipient.email(),
                recipient.fullName(),
                recipient.phone(),
                message
        );
    }
}
