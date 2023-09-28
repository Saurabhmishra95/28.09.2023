package com.experianhealth.ciam.scimapi.service.impl;


import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.RoleMember;
import com.experianhealth.ciam.forgerock.service.ManagedRoleService;
import com.experianhealth.ciam.scimapi.controller.ScimEndpoints;
import com.experianhealth.ciam.scimapi.entity.GroupMember;
import com.experianhealth.ciam.scimapi.entity.Operation;
import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimGroup;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.service.GroupService;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.json.JsonPatch;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger LOGGER = Logger.getLogger(GroupServiceImpl.class.getName());
    private static final String MISSING_REQUIRED_FIELD_GROUP_NAME = "Missing required field: DisplayName";
    RequestContextProvider contextProvider;
    ManagedRoleService managedRoleService;

    @Autowired
    GroupServiceImpl(RequestContextProvider contextProvider, ManagedRoleService managedRoleService) {
        this.contextProvider = contextProvider;
        this.managedRoleService = managedRoleService;
    }

    // OpenIDM endpoint URL

    /**
     * Retrieves a list of groups using the provided authorization header.
     *
     * @param token The access_token for the user
     * @return List of Group objects representing the retrieved groups.
     */
    @Override
    public ScimListResponse<ScimGroup> getGroups(String token) {
        List<Role> forgeRockRoles = managedRoleService.getAll(token);
        List<ScimGroup> scimGroups = convertToScimSchema(forgeRockRoles);

        ScimListResponse<ScimGroup> listResponse = new ScimListResponse<>();

        listResponse.setSchemas(Collections.singletonList("urn:ietf:params:scim:api:messages:2.0:ListResponse"));
        listResponse.setTotalResults(scimGroups.size());
        listResponse.setStartIndex(1);  // adjust according to your pagination logic
        listResponse.setItemsPerPage(scimGroups.size());  // adjust according to your pagination logic
        listResponse.setResources(scimGroups);

        return listResponse;
    }

    /**
     * Creates a new group in ForgeRock IDM using the provided access token and SCIM group representation.
     *
     * @param token          The access token for the user making the request.
     * @param groupToCreate  The SCIM group representation of the group to be created.
     * @return ScimGroup object representing the newly created group.
     */
    public ScimGroup createGroup(String token, ScimGroup groupToCreate) {
        Role createdRole = managedRoleService.create(token, scimGroupToRole(groupToCreate));
        return roleToScimGroup(createdRole);
    }

    /**
     * Converts a SCIM group representation to a ForgeRock IDM Role object.
     *
     * @param group The SCIM group representation to be converted.
     * @return Role object representing the converted group.
     * @throws CIAMInvalidRequestException if the SCIM group representation is missing the required field for the group name.
     */
    Role scimGroupToRole(ScimGroup group) {
        if (StringUtils.isEmpty(group.getDisplayName())) {
            throw new CIAMInvalidRequestException(MISSING_REQUIRED_FIELD_GROUP_NAME);
        }

        Role role = new Role();
        role.setName(group.getDisplayName());
        role.setDescription(group.getDescription());
        return role;
    }


    @Override
    public Optional<ScimGroup> getGroupById(String token, String id) {

        Optional<Role> forgeRockRole = managedRoleService.getById(token, id);
        if (forgeRockRole.isEmpty()) {
            return Optional.empty();
        }

        ScimGroup scimGroup = roleToScimGroup(forgeRockRole.get());

        // Retrieve the group members
        List<GroupMember> groupMembers = getGroupMembers(token, id);

        // Set the group members in the SCIM Group
        scimGroup.setMembers(groupMembers);

        return Optional.of(scimGroup);
    }

    /**
     * Deletes a group based on the provided group ID and returns the deleted group as a SCIM group representation.
     *
     * @param token the access token for the user.
     * @param groupId The ID of the group to be deleted.
     * @return An Optional containing the deleted SCIM group representation, or an empty Optional if the group was not found.
     */
    @Override
    public Optional<ScimGroup> deleteGroup(String token, String groupId) {
        Optional<Role> deletedRole = managedRoleService.delete(token, groupId);
        if (deletedRole.isEmpty()) {
            return Optional.empty();
        }
        // Map the deleted role to a SCIM group representation
        ScimGroup scimGroup = roleToScimGroup(deletedRole.get());


        return Optional.of(scimGroup);
    }

    /**
     * Converts a list of ForgeRockRole objects to a list of Group objects in the SCIM schema.
     *
     * @param forgeRockRoles List of ForgeRockRole objects to be converted.
     * @return List of Group objects representing the groups in the SCIM schema.
     */
    private List<ScimGroup> convertToScimSchema(List<Role> forgeRockRoles) {
        List<ScimGroup> scimGroups = new ArrayList<>();
        for (Role forgeRockRole : forgeRockRoles) {
            scimGroups.add(roleToScimGroup(forgeRockRole));
        }
        return scimGroups;
    }

    private ScimGroup roleToScimGroup(Role role) {
        ScimGroup group = new ScimGroup();
        group.setSchemas(Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:Group"));
        group.setId(role.get_id());
        group.setDisplayName(role.getName());
        group.setDescription(role.getDescription());
        return group;    }

    List<GroupMember> getGroupMembers(String token, String groupId) {
        // Use the ForgeRockIDMService to retrieve the group members
        List<RoleMember> roleMembers = managedRoleService.getRoleMembers(token, groupId);
        return mapGroupMembers(roleMembers);
    }

    // Map the RoleMember objects to GroupMember objects
    List<GroupMember> mapGroupMembers(List<RoleMember> roleMembers){
        List<GroupMember> scimGroupMembers = new ArrayList<>();
        for (RoleMember roleMember : roleMembers) {
            GroupMember groupMember = new GroupMember();
            groupMember.setValue(roleMember.get_refResourceId());

            // Generate the user URL using the base URL and user ID
            String userUrl = contextProvider.getRequestContext().get().getBaseUri() + "/Users/" + roleMember.get_refResourceId();
            groupMember.set$ref(userUrl);

            // Set any other relevant properties
            scimGroupMembers.add(groupMember);
        }
        return scimGroupMembers;
    }

    @Override
    public ScimGroup updateGroup(String token, String groupId, ScimGroup updatedGroup) {
        // First, retrieve the existing group to ensure it exists.
        Optional<ScimGroup> existingGroupOptional = getGroupById(token, groupId);
        if (existingGroupOptional.isEmpty()) {
            // If the group doesn't exist, return null or throw an exception.
            throw new CIAMNotFoundException(ScimEndpoints.GROUPS + "/" + groupId, "Group not found");
        }

        // Update the group attributes with the new values.
        ScimGroup existingGroup = existingGroupOptional.get();
        existingGroup.setDisplayName(updatedGroup.getDisplayName());
        existingGroup.setDescription(updatedGroup.getDescription());

        // Now, convert the SCIM group representation back to a ForgeRock Role object.
        Role updatedRole = scimGroupToRole(existingGroup);

        // Perform the actual update operation using the ForgeRock IDM service.
        Role updatedForgeRockRole = managedRoleService.replace(token, groupId, updatedRole);

        // Finally, convert the updated ForgeRock Role object back to a SCIM group representation and return it.
        return roleToScimGroup(updatedForgeRockRole);
    }
    
    @Override
    public ScimGroup patchUpdateGroup(String token, String groupId, PatchOp patchOp) {
        GroupPatchBuilder patchBuilder = new GroupPatchBuilder();
        for (Operation operation : patchOp.getOperations()) {
            patchBuilder.applyOperation(operation);
        }
        JsonPatch jsonPatch = patchBuilder.build();
System.out.println(jsonPatch);
        // Call your group modification service with the JSON Patch
        // You need to implement this part based on how you interact with ForgeRock IDM
        Role patchedRole = managedRoleService.modify(token, groupId, jsonPatch);

        // Convert the patched role to ScimGroup and return
        return roleToScimGroup(patchedRole);
    }
}
