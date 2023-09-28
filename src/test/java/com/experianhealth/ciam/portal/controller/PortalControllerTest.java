package com.experianhealth.ciam.portal.controller;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.portal.entity.PasswordUpdateRequest;
import com.experianhealth.ciam.portal.service.PortalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PortalControllerTest extends CIAMTestBase {

    @Mock
    private PortalService portalService;

    @InjectMocks
    private PortalController portalController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testVerifyPasswordSuccess() {
        // Given
        PasswordUpdateRequest request = new PasswordUpdateRequest();
        request.setCurrentPassword("oldPassword");
        request.setNewPassword("newPassword");

        ResponseEntity<String> response = portalController.updatePassword("Bearer token", request);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Password updated successfully.", response.getBody());
    }



}
