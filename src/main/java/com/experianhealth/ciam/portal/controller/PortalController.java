package com.experianhealth.ciam.portal.controller;

import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.PasswordUpdateRequest;
import com.experianhealth.ciam.portal.entity.PortalConfiguration;
import com.experianhealth.ciam.portal.service.PortalService;
import com.experianhealth.ciam.scimapi.utils.AuthorizationUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(PortalController.PORTAL_PATH)
public class PortalController {

    public static final String CONFIGURATION_PATH = "/configuration";
    public static final String UPDATEPASSWORD_PATH = "/updatepassword";
    public static final String PORTAL_PATH = "/portal";
    public static final String APPLICATION_DETAILS_PATH = "/applicationdetails";

    @Autowired
    private PortalService portalService;

    @GetMapping(CONFIGURATION_PATH)
    public ResponseEntity<PortalConfiguration> getConfiguration() {
        return ResponseEntity.ok(portalService.getConfiguration());
    }

    @PostMapping(UPDATEPASSWORD_PATH)
    public ResponseEntity<String> updatePassword(
            @RequestHeader(value = "Authorization", required = false) String bearerToken,
            @RequestBody PasswordUpdateRequest verificationRequest) {
        portalService.updatePassword(
                AuthorizationUtils.validateBearerToken(Optional.ofNullable(bearerToken)),
                verificationRequest.getCurrentPassword(),
                verificationRequest.getNewPassword()
        );
        return ResponseEntity.ok("Password updated successfully.");
    }

    @GetMapping(APPLICATION_DETAILS_PATH)
    public ResponseEntity<List<ApplicationSection>> getApplicationDetails(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken) {
        String token = AuthorizationUtils.validateBearerToken(bearerToken);
        List<ApplicationSection> responses = portalService.getApplicationDetails(token);
        return ResponseEntity.ok(responses);
    }
}