package com.investflow.portfolio;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PortfolioServiceApplicationTests {

    @Test
    void applicationClassPresent() {
        PortfolioServiceApplication app = new PortfolioServiceApplication();
        assertNotNull(app);
    }
}
