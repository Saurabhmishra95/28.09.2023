package com.experianhealth.ciam.portal.service.impl;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.exception.CIAMPasswordException;
import com.experianhealth.ciam.forgerock.model.Application;
import com.experianhealth.ciam.forgerock.model.ApplicationDetails;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.forgerock.service.ForgeRockAMService;
import com.experianhealth.ciam.forgerock.service.ManagedApplicationService;
import com.experianhealth.ciam.forgerock.service.ManagedUserService;

import com.experianhealth.ciam.portal.entity.ApplicationSection;
import com.experianhealth.ciam.portal.utility.ApplicationDetailsMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PortalServiceImplTest extends CIAMTestBase {

    @Mock
    private ForgeRockAMService amService;

    @Mock
    private ManagedUserService managedUserService;

    private PortalServiceImpl portalService;

    @Mock
    private ApplicationDetailsMapper applicationDetailsMapper;

    @Mock
    private ManagedApplicationService managedApplicationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        portalService = new PortalServiceImpl(amService, managedUserService, managedApplicationService, applicationDetailsMapper);
    }

    @Test
    void testUpdatePasswordSuccess() {
        String token = "fakeToken";
        String currentPassword = "oldPassword";
        String newPassword = "newPassword";

        User userInfo = new User();
        userInfo.setUserName("username");
        userInfo.set_id("userId");

        when(amService.getUserInfo(token)).thenReturn(userInfo);
        when(amService.getAccessToken(any(), any(), any(), any())).thenReturn("fakeAccessToken");

        portalService.updatePassword(token, currentPassword, newPassword);

        
    }

    @Test
    void testUpdatePasswordInvalidCurrentPassword() {
        String token = "fakeToken";
        String currentPassword = "invalidPassword";
        String newPassword = "newPassword";

        User userInfo = new User();
        userInfo.setUserName("username");
        userInfo.set_id("userId");

        when(amService.getUserInfo(token)).thenReturn(userInfo);
        when(amService.getAccessToken(any(), any(), any(), any())).thenThrow(new RuntimeException("invalid_grant"));

        assertThrows(CIAMPasswordException.class, () -> portalService.updatePassword(token, currentPassword, newPassword));
    }

    @Test
    void testUpdatePasswordMatchingNewAndCurrent() {
        String token = "fakeToken";
        String currentPassword = "oldPassword";
        String newPassword = "oldPassword";

        User userInfo = new User();
        userInfo.setUserName("username");
        userInfo.set_id("userId");

        when(amService.getUserInfo(token)).thenReturn(userInfo);
        when(amService.getAccessToken(any(), any(), any(), any())).thenReturn("fakeAccessToken");

        assertThrows(CIAMPasswordException.class, () -> portalService.updatePassword(token, currentPassword, newPassword));
    }
    @Test
    void testGetApplicationDetailsSuccess() {
        String token = "sampleToken";

        User user = new User();
        user.set_id("sampleUserId");

        User detailedUser = new User();
        List<Application> effectiveApplications = Arrays.asList(new Application());
        detailedUser.setEffectiveApplications(effectiveApplications);

        ApplicationDetails mockAppDetails = new ApplicationDetails();
        mockAppDetails.set_id("mockAppDetailsId");
        mockAppDetails.setName("mockAppName");

        List<ApplicationDetails> mockAppDetailsList = Arrays.asList(mockAppDetails);

        when(amService.getUserInfo(token)).thenReturn(user);
        when(managedUserService.getById(token, user.get_id())).thenReturn(java.util.Optional.of(detailedUser));
        when(managedApplicationService.search(anyString(), any())).thenReturn(mockAppDetailsList);

        List<ApplicationSection> resultList = portalService.getApplicationDetails(token);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(2, resultList.size()); // Since two ApplicationSection objects are returned
        assertEquals("myApps", resultList.get(0).getSection());
        assertEquals("availableApps", resultList.get(1).getSection());

        verify(amService, times(1)).getUserInfo(token);
        verify(managedUserService, times(1)).getById(token, user.get_id());
    }

    @Test
    void testGetApplicationDetailsNoAppsFound() {
        String token = "sampleToken";
        User user = new User();
        user.set_id("sampleUserId");
        User detailedUser = new User();
        detailedUser.setEffectiveApplications(Collections.emptyList());

        when(amService.getUserInfo(token)).thenReturn(user);
        when(managedUserService.getById(token, user.get_id())).thenReturn(java.util.Optional.of(detailedUser));
        when(managedApplicationService.search(anyString(), any())).thenReturn(Collections.emptyList());

        List<ApplicationSection> resultList = portalService.getApplicationDetails(token);

        assertNotNull(resultList);
        assertFalse(resultList.isEmpty());
        assertEquals(2, resultList.size()); // Since two ApplicationSection objects are returned
        assertEquals("myApps", resultList.get(0).getSection());
        assertTrue(resultList.get(0).getApps().isEmpty());
        assertEquals("availableApps", resultList.get(1).getSection());
        assertTrue(resultList.get(1).getApps().isEmpty());
    }

}
