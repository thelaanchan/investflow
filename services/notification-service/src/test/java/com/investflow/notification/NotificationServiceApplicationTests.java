package com.investflow.notification;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NotificationServiceApplicationTests {

    @Test
    void applicationClassPresent() {
        NotificationServiceApplication app = new NotificationServiceApplication();
        assertNotNull(app);
    }
}
