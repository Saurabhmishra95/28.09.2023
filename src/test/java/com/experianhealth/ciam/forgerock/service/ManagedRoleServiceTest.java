package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.CIAMTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ManagedRoleServiceTest extends CIAMTestBase {

    @InjectMocks
    private ManagedRoleServiceImpl service;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testInjections() {
        assertNotNull(service);
    }

}
