package com.experianhealth.ciam.scimapi.service.impl;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.RequestContext;
import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.RoleMember;
import com.experianhealth.ciam.forgerock.service.ManagedRoleService;
import com.experianhealth.ciam.scimapi.entity.GroupMember;
import com.experianhealth.ciam.scimapi.entity.ScimGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class GroupServiceImplTest extends CIAMTestBase {

    private String expectedScimBaseUrl;

    @Mock
    private ManagedRoleService managedRoleService;
    @Mock
    private RequestContextProvider contextProvider;

    @InjectMocks
    private GroupServiceImpl groupService ;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        RequestContext requestContext = mock(RequestContext.class);
        when(requestContext.getBaseUri()).thenReturn(CIAMTestBase.MOCK_SCIM_BASEURL);
        when(contextProvider.getRequestContext()).thenReturn(Optional.of(requestContext));
    }
    @Test
    public void testGetGroupById_withBasicInformation() {
        // Arrange
        String testToken = "testBearerToken";
        String testId = "123456789";

        Role testRole = new Role();
        testRole.set_id(testId);
        testRole.setName("Test Role");
        testRole.setDescription("This is a test role");
        System.out.println("idmService: " + managedRoleService.getClass());
        System.out.println("groupService: " + groupService.getClass());
        System.out.println("groupService.managedRoleService: "  + groupService.managedRoleService.getClass());

        when(managedRoleService.getById(testToken, testId)).thenReturn(Optional.of(testRole));

        // Act
        Optional<ScimGroup> result = groupService.getGroupById(testToken, testId);
        assertTrue(result.isPresent());
        ScimGroup group = result.get();
        // Assert
        assertEquals(testId, group.getId());
        assertEquals("Test Role", group.getDisplayName());
        assertEquals("This is a test role", group.getDescription());
    }

    @Test
    void testGetGroupById_withRoleMembers() {
        // Mocking input data
        String mockToken = "token";
        String groupId = "group1";

        // Mocking IDM service response
        Role mockRole = new Role();
        mockRole.set_id(groupId);
        mockRole.setName("Group 1");
        mockRole.setDescription("Test group");
        when(managedRoleService.getById(eq(mockToken), eq(groupId))).thenReturn(Optional.of(mockRole));

        // Mocking group members
        List<RoleMember> mockGroupMembers = Collections.singletonList(new RoleMember());
        when(managedRoleService.getRoleMembers(eq(mockToken), eq(groupId))).thenReturn(mockGroupMembers);

        // Call the method under test
        Optional<ScimGroup> result = groupService.getGroupById(mockToken, groupId);

        // Verify the result
        assertEquals(true, result.isPresent());
        ScimGroup scimGroup = result.get();

        // Verify the call to getRoleMembers
        verify(managedRoleService, times(1)).getRoleMembers(eq(mockToken), eq(groupId));
    }

    @Test
    void testMapGroupMembers() {
        // Mocking input data
        RoleMember mockRoleMember = new RoleMember();
        mockRoleMember.set_refResourceId("user1");

        // Call the method under test
        List<GroupMember> result = groupService.mapGroupMembers(Collections.singletonList(mockRoleMember));
        // Verify the result
        assertEquals(1, result.size());
        GroupMember groupMember = result.get(0);
        assertEquals("user1", groupMember.getValue());
        assertEquals(MOCK_SCIM_BASEURL + "/Users/user1", groupMember.get$ref());
    }

    @Test
    public void testCreateGroup_withValidGroup() {
        // Arrange
        String testToken = "testBearerToken";
        ScimGroup testGroup = new ScimGroup();
        testGroup.setDisplayName("Test Group");
        testGroup.setDescription("This is a test group");

        Role createdRole = new Role();
        createdRole.setName(testGroup.getDisplayName());
        createdRole.setDescription(testGroup.getDescription());

        when(managedRoleService.create(any(), any())).thenReturn(createdRole);

        // Act
        ScimGroup result = groupService.createGroup(testToken, testGroup);

        // Assert
        assertEquals("Test Group", result.getDisplayName());
        assertEquals("This is a test group", result.getDescription());
    }

    @Test
    public void testCreateGroup_withMissingDisplayName() {
        // Arrange
        String testToken = "testBearerToken";
        ScimGroup testGroup = new ScimGroup(); // DisplayName is missing

        // Act and Assert
        assertThrows(CIAMInvalidRequestException.class, () -> groupService.createGroup(testToken, testGroup));
    }


    @Test
    public void testDeleteGroup_SuccessfulDeletion() {
        // Prepare the data for the test
        String token = "dummyAccessToken";
        String groupId = "dummyGroupId";

        // Create a mock ForgeRock Role to return on delete
        Role deletedRole = new Role();
        deletedRole.set_id(groupId);
        deletedRole.setName("Test Group");
        deletedRole.setDescription("This is a test group");

        // Mock the behavior of ForgeRockIDMService to return the deletedRole on delete
        when(managedRoleService.delete(anyString(), anyString())).thenReturn(Optional.of(deletedRole));

        // Perform the deletion
        Optional<ScimGroup> deletedGroup = groupService.deleteGroup(token, groupId);

        // Verify the interaction with ForgeRockIDMService
        verify(managedRoleService, times(1)).delete(eq(token), eq(groupId));

        // Verify the result of the deletion
        assertEquals(true, deletedGroup.isPresent());
        assertEquals(groupId, deletedGroup.get().getId());
        assertEquals("Test Group", deletedGroup.get().getDisplayName());
        assertEquals("This is a test group", deletedGroup.get().getDescription());
    }

    @Test
    public void testDeleteGroup_GroupNotFound() {
        // Prepare the data for the test
        String token = "dummyAccessToken";
        String groupId = "nonExistentGroupId";

        // Mock the behavior of ForgeRockIDMService to return an empty Optional (group not found)
        when(managedRoleService.delete(anyString(), anyString())).thenReturn(Optional.empty());

        // Perform the deletion
        Optional<ScimGroup> deletedGroup = groupService.deleteGroup(token, groupId);

        // Verify the interaction with ForgeRockIDMService
        verify(managedRoleService, times(1)).delete(eq(token), eq(groupId));

        // Verify the result of the deletion (group not found)
        assertEquals(false, deletedGroup.isPresent());
    }


    @Test
    public void testUpdateGroup_SuccessfulUpdate() {
        // Prepare the data for the test
        String token = "dummyAccessToken";
        String groupId = "dummyGroupId";

        // Create a mock ForgeRock Role to return on getGroupById
        Role existingRole = new Role();
        existingRole.set_id(groupId);
        existingRole.setName("Test Group");
        existingRole.setDescription("This is a test group");

        // Mock the behavior of ForgeRockIDMService to return the existingRole on getGroupById
        when(managedRoleService.getById(anyString(), anyString())).thenReturn(Optional.of(existingRole));

        // Create a mock ForgeRock Role to return on updateRole
        Role updatedRole = new Role();
        updatedRole.set_id(groupId);
        updatedRole.setName("Updated Group");
        updatedRole.setDescription("This is the updated group description");

        // Mock the behavior of ForgeRockIDMService to return the updatedRole on updateRole
        when(managedRoleService.replace(anyString(), anyString(), any())).thenReturn(updatedRole);

        // Prepare the updated group data
        ScimGroup updatedGroup = new ScimGroup();
        updatedGroup.setDisplayName("Updated Group");
        updatedGroup.setDescription("This is the updated group description");

        // Perform the group update
        ScimGroup result = groupService.updateGroup(token, groupId, updatedGroup);

        // Verify the interaction with ForgeRockIDMService
        verify(managedRoleService, times(1)).getById(eq(token), eq(groupId));
        verify(managedRoleService, times(1)).replace(eq(token), eq(groupId), any());

        // Verify the result of the group update
        assertEquals(groupId, result.getId());
        assertEquals("Updated Group", result.getDisplayName());
        assertEquals("This is the updated group description", result.getDescription());
    }

    @Test
    public void testUpdateGroup_GroupNotFound() {
        // Prepare the data for the test
        String token = "dummyAccessToken";
        String groupId = "nonExistentGroupId";

        // Mock the behavior of ForgeRockIDMService to return an empty Optional (group not found)
        when(managedRoleService.getById(anyString(), anyString())).thenReturn(Optional.empty());

        // Prepare the updated group data
        ScimGroup updatedGroup = new ScimGroup();
        updatedGroup.setDisplayName("Updated Group");
        updatedGroup.setDescription("This is the updated group description");

        // Perform the group update
        assertThrows(CIAMNotFoundException.class, () -> groupService.updateGroup(token, groupId, updatedGroup));

        // Verify the interaction with ForgeRockIDMService
        verify(managedRoleService, times(1)).getById(eq(token), eq(groupId));
        verify(managedRoleService, never()).replace(eq(token), eq(groupId), any());
    }
}

