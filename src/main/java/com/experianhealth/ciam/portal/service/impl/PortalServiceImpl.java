package com.experianhealth.ciam.portal.service.impl;

import javax.json.Json;
import javax.json.JsonPatch;
import javax.json.JsonPatchBuilder;

import com.experianhealth.ciam.EnvironmentSettings;
import com.experianhealth.ciam.forgerock.model.*;
import com.experianhealth.ciam.forgerock.service.ManagedApplicationService;
import com.experianhealth.ciam.forgerock.service.ManagedUserService;
import com.experianhealth.ciam.portal.entity.PortalConfiguration;
import com.experianhealth.ciam.portal.service.PortalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.experianhealth.ciam.exception.CIAMPasswordException;
import com.experianhealth.ciam.forgerock.service.ForgeRockAMService;




import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.entity.AppDetail;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.portal.utility.ApplicationDetailsMapper;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PortalServiceImpl implements PortalService {

    @Autowired
    private ForgeRockAMService amService;

    @Autowired
    private ManagedUserService managedUserService;

    @Autowired
    private ManagedApplicationService managedApplicationService;

    @Autowired
    private ApplicationDetailsMapper applicationDetailsMapper;

    private final String clientId;
    private final String clientSecret;
    private  final PortalConfiguration configuration;

    @Autowired
    public PortalServiceImpl(ForgeRockAMService amService, ManagedUserService managedUserService,ManagedApplicationService managedApplicationService,ApplicationDetailsMapper applicationDetailsMapper) {
        this.amService = amService;
        this.managedUserService = managedUserService;
        this.managedApplicationService=managedApplicationService;
        this.applicationDetailsMapper=applicationDetailsMapper;
        clientId = EnvironmentSettings.getPortalClientId();
        clientSecret = EnvironmentSettings.getPortalClientSecret();
        configuration = new PortalConfiguration();
        configuration.setAmBaseUrl(EnvironmentSettings.getAmBaseUrl());
        configuration.setIdmBaseUrl(EnvironmentSettings.getIdmBaseUrl());
        configuration.setClientId(clientId);
    }

    public void updatePassword(String token, String currentPassword, String newPassword) {
    	
    	
        // Step 1: Retrieve user information from the AM userinfo endpoint
        User userInfo = amService.getUserInfo(token);
        String userName = userInfo.getUserName(); // Assuming getUsername() method exists
        String id = userInfo.get_id();
        
        // Step 2: Verify the previous password using ForgeRockAMService
        try {
            amService.getAccessToken(clientId, clientSecret, userName, currentPassword);
        } catch (RuntimeException e) {
            if (e.getMessage().contains("invalid_grant")) {
                throw new CIAMPasswordException("Previous password is invalid");
            } else {
                throw e; // Re-throw other runtime exceptions
            }
        }

        // Step 3: Check if the newPassword matches the previousPassword
        if (newPassword.equals(currentPassword)) {
            throw new CIAMPasswordException("New password must not match previous password");
        }

        // Step 4: Check if the newPassword contains the userName
        if (newPassword.contains(userName)) {
            throw new CIAMPasswordException("New password must not contain the username");
        }

        // Step 5: Check if the newPassword matches password verification rules (implement your rules here)
        if (!meetsPasswordVerificationRules(newPassword)) {
            throw new CIAMPasswordException("New password does not meet password verification rules");
        }   

        // Step 6: Update the password using ForgeRockIDMService
        managedUserService.modify(token, id, createPasswordPatch(newPassword));
    }

    @Override
    public PortalConfiguration getConfiguration() {
        return configuration;
    }

    private JsonPatch createPasswordPatch(String newPassword) {
        // Create a JsonPatch to update the password attribute
        JsonPatchBuilder patchBuilder = Json.createPatchBuilder();
        JsonPatch patch = patchBuilder.add("/password", newPassword).build();
        return patch;
    }

    // We can  implement other password verification rules here
    private boolean meetsPasswordVerificationRules(String newPassword) {
        // Example: Minimum length of 8 characters
        return newPassword.length() >= 8;
    }

    @Override
    public List<ApplicationSection> getApplicationDetails(String token) {
        User user = amService.getUserInfo(token);
        User detailedUser = managedUserService.getById(token, user.get_id()).orElse(null);
        List<Application> effectiveApplications = detailedUser.getEffectiveApplications();
        FRQuery appQueryIds = FRQuery.Builder.create().withFilterExpression(
                FRQueryFilter.in("_id", getApplicationIds(effectiveApplications))
        ).build();
        List<ApplicationDetails> applicationDetailsList = managedApplicationService.search(token, appQueryIds);
        List<AppDetail> transformedApps = applicationDetailsList.stream()
                .map(applicationDetailsMapper::mapToAppDetail)
                .collect(Collectors.toList());

        ApplicationSection myAppsResponse = new ApplicationSection();
        myAppsResponse.setSection("myApps");
        myAppsResponse.setApps(transformedApps);


        ApplicationSection availableAppsResponse = new ApplicationSection();
        availableAppsResponse.setSection("availableApps");
        availableAppsResponse.setApps(new ArrayList<>());

        return Arrays.asList(myAppsResponse, availableAppsResponse);
    }

    private List<String> getApplicationIds(List<Application> applications) {
        return applications.stream().map(Application::get_id).collect(Collectors.toList());
    }
}
