package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.CIAMTestBase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

public class ManagedUserServiceTest extends CIAMTestBase {

    @InjectMocks
    private ManagedUserServiceImpl userService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void testInjections() {
        assertNotNull(userService);
    }

}
