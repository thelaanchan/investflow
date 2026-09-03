package com.investflow.ai;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class AiServiceApplicationTests {

    @Test
    void applicationClassPresent() {
        AiServiceApplication app = new AiServiceApplication();
        assertNotNull(app);
    }
}
