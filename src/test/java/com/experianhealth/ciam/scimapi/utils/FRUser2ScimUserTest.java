package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.EnvironmentSettings;
import com.experianhealth.ciam.forgerock.model.EffectiveRole;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.scimapi.controller.ScimEndpoints;
import com.experianhealth.ciam.scimapi.entity.Meta;
import com.experianhealth.ciam.scimapi.entity.ScimGroupReference;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static com.experianhealth.ciam.CIAMTestBase.MOCK_SCIM_BASEURL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FRUser2ScimUserTest extends CIAMTestBase {

    @Test
    public void testMapToScim() {
        // Given
        User user = new User();
        user.set_id("123");
        user.setUserName("testUser");
        user.setDisplayName("testdisplayname");
        user.setGivenName("Test");
        user.setSn("User");
        user.setMail("testUser@example.com");
        user.setTelephoneNumber("1234567890");
        user.setPostalAddress("123 Main St");
        user.setStateProvince("State");
        user.setPostalCode("12345");
        user.setCountry("Country");

        // When
        ScimUser scimUser = FRUser2ScimUser.mapToScimUser(user, CIAMTestBase.MOCK_SCIM_BASEURL);

        // Then
        assertEquals("123", scimUser.getId());
        assertEquals("testUser", scimUser.getUserName());
        assertEquals("testdisplayname", scimUser.getDisplayName());
        assertEquals("Test", scimUser.getName().getGivenName());
        assertEquals("User", scimUser.getName().getFamilyName());
        assertEquals("testUser@example.com", scimUser.getEmails().get(0).getValue());
        assertEquals("1234567890", scimUser.getPhoneNumbers().get(0).getValue());
        assertEquals("123 Main St, State, 12345, Country", scimUser.getAddress().getFormatted());
    }

    @Test
    public void testCreateRoles() {
        // Create an effective role
        EffectiveRole role1 = new EffectiveRole();
        role1.set_refResourceId("123");

        EffectiveRole role2 = new EffectiveRole();
        role2.set_refResourceId("456");

        // Create a user with the effective role
        User user = new User();
        user.setEffectiveRoles(Arrays.asList(role1, role2));

        // Call the method under test
        List<ScimGroupReference> scimRoles = FRUser2ScimUser.createRoles(user,MOCK_SCIM_BASEURL);

        // Assert that the returned ScimGroupReference has correct attributes
        assertNotNull(scimRoles);
        assertEquals(2, scimRoles.size());

        ScimGroupReference scimRole1 = scimRoles.get(0);
        assertEquals("123", scimRole1.getValue());
        assertEquals(MOCK_SCIM_BASEURL + "/Groups/123", scimRole1.get$ref());

        ScimGroupReference scimRole2 = scimRoles.get(1);
        assertEquals("456", scimRole2.getValue());
        assertEquals(MOCK_SCIM_BASEURL + "/Groups/456", scimRole2.get$ref());
    }

    @Test
    public void testCreateMeta() {
        // Given
        User user = new User();
        user.set_id("123");
        user.set_rev("1"); // Assuming the version is "1"
        String scimBaseUri = CIAMTestBase.MOCK_SCIM_BASEURL;
        // When
        Meta meta = FRUser2ScimUser.createMeta(user,scimBaseUri);

        // Then
        assertEquals("User", meta.getResourceType());
        assertEquals("1", meta.getVersion());
        assertEquals(scimBaseUri + ScimEndpoints.USERS + "/123", meta.getLocation());
    }

    @Test
    public void testMapAccountStatusActive() {
        // Given
        User user = new User();
        user.setAccountStatus("active");

        // When
        boolean isActive = FRUser2ScimUser.mapAccountStatus(user);

        // Then
        assertEquals(true, isActive);
    }

    @Test
    public void testMapAccountStatusInactive() {
        // Given
        User user = new User();
        user.setAccountStatus("inactive");

        // When
        boolean isActive = FRUser2ScimUser.mapAccountStatus(user);

        // Then
        assertEquals(false, isActive);
    }

    @Test
    public void testMapAccountStatusNull() {
        // Given
        User user = new User();
        user.setAccountStatus(null);

        // When
        boolean isActive = FRUser2ScimUser.mapAccountStatus(user);

        // Then
        assertEquals(false, isActive);
    }
}
