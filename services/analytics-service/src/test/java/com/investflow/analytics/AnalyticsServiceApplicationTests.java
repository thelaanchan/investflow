package com.investflow.analytics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AnalyticsServiceApplicationTests {

    @Test
    void applicationClassPresent() {
        AnalyticsServiceApplication app = new AnalyticsServiceApplication();
        assertNotNull(app);
    }
}
