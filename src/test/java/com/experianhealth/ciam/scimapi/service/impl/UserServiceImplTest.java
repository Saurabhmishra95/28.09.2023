package com.experianhealth.ciam.scimapi.service.impl;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.RequestContext;
import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.forgerock.service.ManagedUserService;
import com.experianhealth.ciam.scimapi.entity.Email;
import com.experianhealth.ciam.scimapi.entity.Name;
import com.experianhealth.ciam.scimapi.entity.Operation;
import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import com.experianhealth.ciam.scimapi.utils.ScimUser2FRUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.json.JsonPatch;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserServiceImplTest extends CIAMTestBase {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private ManagedUserService managedUserService;

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
    public void testInjections() {
        assertNotNull(userService);
    }
    @Test
    void testGetUserById() {
        // Create a sample user and map it to a ScimUser
        User sampleUser = new User();
        sampleUser.set_id("123");
        sampleUser.setUserName("john.doe");

        // Stub the getUserById method of the IDM service to return the sample user
        when(managedUserService.getById(anyString(), anyString())).thenReturn(Optional.of(sampleUser));

        // Invoke the getUserById method with the required parameters
        Optional<ScimUser> result = userService.getUserById("Bearer token", "123");
        assertTrue(result.isPresent());
        ScimUser scimUser = result.get();
        // Verify the expected behavior and assertions
        assertEquals("123", scimUser.getId());
        assertEquals("john.doe", scimUser.getUserName());
    }

    @Test
    void testUpdateUserExistingUser() {
        // Create a sample user and map it to a ScimUser
        User existingUser = new User();
        existingUser.set_id("123");
        existingUser.setUserName("saurabh.mishra");

        ScimUser updatedUser = new ScimUser();
        updatedUser.setId("123");
        updatedUser.setUserName("mishra.saurabh@example.com");

        Email email = new Email();
        email.setValue("mishra.saurabh@example.com");
        updatedUser.setEmails(Collections.singletonList(email));

        Name name = new Name();
        name.setGivenName("mishra");
        name.setFamilyName("saurabh");
        updatedUser.setName(name);


        when(managedUserService.getById(anyString(), anyString())).thenReturn(Optional.of(existingUser));


        when(managedUserService.replace(anyString(), anyString(), any(User.class)))
                .thenReturn(ScimUser2FRUser.mapToUser(updatedUser));  // Convert ScimUser to User


        ScimUser result = userService.updateUser("Bearer token", "123", updatedUser);


        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals("mishra.saurabh@example.com", result.getUserName());
    }

    @Test
    void testUpdateUserNonExistingUser() {

        when(managedUserService.getById(anyString(), anyString())).thenReturn(Optional.empty());


        ScimUser updatedUser = new ScimUser();
        updatedUser.setId("123");
        updatedUser.setUserName("jane.smith");


        assertThrows(CIAMNotFoundException.class, () -> {
            userService.updateUser("Bearer token", "123", updatedUser);
        });
    }

    @Test
    void testPatchUpdateUserName() {
        // Sample Data
        User sampleUser = new User();
        sampleUser.set_id("123");

        String expectedUserName = "saurabh.updated";
        sampleUser.setUserName(expectedUserName);

        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("userName");
        operation.setValue(expectedUserName);

        PatchOp patchOp = new PatchOp();
        patchOp.setOperations(Collections.singletonList(operation));
        when(managedUserService.modify(anyString(), anyString(), any(JsonPatch.class))).thenReturn(sampleUser);
        ScimUser result = userService.patchupdateUser("Bearer token", "123", patchOp);
        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals(expectedUserName, result.getUserName());
    }

    @Test
    void testPatchUpdatedisplayName() {
        User sampleUser = new User();
        sampleUser.set_id("123");
        sampleUser.setDisplayName("John Doe");
        String expecteddisplayName = "john.doe.updated";
        sampleUser.setUserName(expecteddisplayName);

        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("userName");
        operation.setValue("john.doe.updated");

        PatchOp patchOp = new PatchOp();
        patchOp.setOperations(Collections.singletonList(operation));

        when(managedUserService.modify(anyString(), anyString(), any(JsonPatch.class))).thenReturn(sampleUser);

        ScimUser result = userService.patchupdateUser("Bearer token", "123", patchOp);

        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals(expecteddisplayName, result.getUserName());
        assertEquals("John Doe", result.getDisplayName());
    }
    @Test
    void testPatchUpdateTimezone() {

        User sampleUser = new User();
        sampleUser.set_id("123");
        sampleUser.setPreferredTimezone("UTC");

        String expectedTimezone = "PST";
        sampleUser.setPreferredTimezone(expectedTimezone);

        Operation operation = new Operation();
        operation.setOp("replace");
        operation.setPath("timezone");
        operation.setValue(expectedTimezone);

        PatchOp patchOp = new PatchOp();
        patchOp.setOperations(Collections.singletonList(operation));

        when(managedUserService.modify(anyString(), anyString(), any(JsonPatch.class))).thenReturn(sampleUser);

        ScimUser result = userService.patchupdateUser("Bearer token", "123", patchOp);

        assertNotNull(result);
        assertEquals("123", result.getId());
        assertEquals(expectedTimezone, result.getTimezone());
    }

}
