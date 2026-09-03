package com.investflow.investment;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class InvestmentServiceApplicationTests {

    @Test
    void applicationClassPresent() {
        InvestmentServiceApplication app = new InvestmentServiceApplication();
        assertNotNull(app);
    }
}
