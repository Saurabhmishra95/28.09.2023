package com.experianhealth.ciam.scimapi.service.impl;


import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.RequestContext;
import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.forgerock.service.ForgeRockAMService;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import com.experianhealth.ciam.scimapi.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MeServiceImplTest {

    @InjectMocks
    private MeServiceImpl meServiceImpl;

    @Mock
    private ForgeRockAMService forgeRockAMService;

    @Mock
    private UserService userService;
    @Mock
    private RequestContextProvider contextProvider;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        RequestContext requestContext = mock(RequestContext.class);
        when(requestContext.getBaseUri()).thenReturn(CIAMTestBase.MOCK_SCIM_BASEURL);
        when(contextProvider.getRequestContext()).thenReturn(Optional.of(requestContext));
    }
    @Test
    public void testGetMe() {
        // Given
        String token = "sampleToken";
        User mockUserInfo = new User();
        mockUserInfo.set_id("12345");
        mockUserInfo.setUserName("testUser");

        ScimUser mockScimUser = mock(ScimUser.class);
        when(forgeRockAMService.getUserInfo(token)).thenReturn(mockUserInfo);
        when(userService.getUserById(token, "12345")).thenReturn(Optional.of(mockScimUser));

        // When
        ScimUser scimUser = meServiceImpl.getMe(token);

        // Then
        assertSame(mockScimUser, scimUser);
    }

    
}
