package com.experianhealth.ciam;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ApiApplicationTests extends CIAMTestBase {

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

	@Test
	void contextLoads() {
	}

}
