package com.investflow.user;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserServiceApplicationTests {

    @Test
    void applicationClassPresent() {
        UserServiceApplication app = new UserServiceApplication();
        assertNotNull(app);
    }
}
