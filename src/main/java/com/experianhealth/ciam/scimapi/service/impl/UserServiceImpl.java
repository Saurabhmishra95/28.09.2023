package com.experianhealth.ciam.scimapi.service.impl;

import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.forgerock.model.FRQuery;
import com.experianhealth.ciam.forgerock.model.FRQueryFilter;
import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.forgerock.service.ManagedRoleService;
import com.experianhealth.ciam.forgerock.service.ManagedUserService;
import com.experianhealth.ciam.scimapi.entity.Operation;
import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimGroupReference;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import com.experianhealth.ciam.scimapi.service.UserService;
import com.experianhealth.ciam.scimapi.utils.FRUser2ScimUser;
import com.experianhealth.ciam.scimapi.utils.ScimUser2FRUser;
import com.experianhealth.ciam.scimapi.utils.UserPatchBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.json.JsonPatch;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = Logger.getLogger(UserServiceImpl.class.getName());
    public UserServiceImpl() {

    }
    @Autowired
    private ManagedUserService userService;

    @Autowired
    private ManagedRoleService roleService;

    @Autowired
    private RequestContextProvider contextProvider;

    private String getScimBaseUri() {
        return contextProvider.getRequestContext().get().getBaseUri();
    }
    /**
     * Retrieves all users using the provided authorization header.
     *
     * @param  token  The access token for the user
     * @return List of ScimUser objects representing the retrieved users.
     */
    @Override
    public ScimListResponse<ScimUser> getAllUsers(String token) {
        List<ScimUser> scimUsers = FRUser2ScimUser.mapToScimUsers(userService.getAll(token),getScimBaseUri());

        ScimListResponse<ScimUser> listResponse = new ScimListResponse<ScimUser>();
        listResponse.setSchemas(Collections.singletonList("urn:ietf:params:scim:api:messages:2.0:ListResponse"));
        listResponse.setTotalResults(scimUsers.size());
        listResponse.setStartIndex(1);  // adjust according to your pagination logic
        listResponse.setItemsPerPage(scimUsers.size());  // adjust according to your pagination logic
        listResponse.setResources(scimUsers);

        return listResponse;
    }

    /**
     * Retrieves a specific user by their ID and maps it to a SCIM user representation.
     *
     * @param token  The access token for the user
     * @param userId              The ID of the user to retrieve.
     * @return A Response containing the SCIM user representation.
     */
    @Override
    public Optional<ScimUser> getUserById(String token, String userId) {
        // Retrieve the user from the IDM service using the bearer token and user ID
        Optional<User> user = userService.getById(token, userId);
        if (user.isEmpty()) return Optional.empty();
        // Map the retrieved user to a SCIM user representation
        ScimUser scimUser = FRUser2ScimUser.mapToScimUser(user.get(),getScimBaseUri());

        List<ScimGroupReference> groups = scimUser.getGroups();
        if(! CollectionUtils.isEmpty(groups)){
            FRQuery roleQueryIds = FRQuery.Builder.create().withFilterExpression(
                    FRQueryFilter.in("_id",getIds(groups))
            ).build();
            List<Role> roles = roleService.search(token,roleQueryIds);
            Map<String, ScimGroupReference> roleMap = toMap(scimUser.getGroups());
            for(Role role : roles){
                roleMap.get(role.get_id()).setDisplay(role.getName());
            }
            System.out.println(roles);
        }
        return Optional.of(scimUser);
    }
    Map<String, ScimGroupReference> toMap(List<ScimGroupReference> list){
        return list.stream()
                .collect(Collectors.toMap(ScimGroupReference::getValue, Function.identity()));
    }
    List<String> getIds(Collection<ScimGroupReference> groups) {
        return groups.stream().map(group -> { return group.getValue(); }).collect(Collectors.toList());
    }
    @Override
    public ScimUser createUser(String token, ScimUser userToCreate) {
        User createdUser = userService.create(token, ScimUser2FRUser.mapToUser(userToCreate));
        return FRUser2ScimUser.mapToScimUser(createdUser,getScimBaseUri());
    }

    @Override
    public Optional<ScimUser> deleteUser(String token, String userId) {
        Optional<User> deletedUser = userService.delete(token, userId);
        if (deletedUser.isEmpty()) return Optional.empty();
        // Map the retrieved user to a SCIM user representation
        ScimUser scimUser = FRUser2ScimUser.mapToScimUser(deletedUser.get(),getScimBaseUri());

        return Optional.of(scimUser);
    }

    @Override
    public ScimUser updateUser(String token, String userId, ScimUser updatedUser) {
        Optional<User> existingUser = userService.getById(token, userId);
        if (existingUser.isEmpty()) {
            throw new CIAMNotFoundException("/Users/" + userId, "User not found");
        }

        // Map the updated SCIM user to ForgeRock user
        User userToUpdate = ScimUser2FRUser.mapToUser(updatedUser);

        // Perform the update using ForgeRock IDM service
        User updatedUserObj = userService.replace(token, userId, userToUpdate);

        // Map the updated ForgeRock user back to SCIM user
        return FRUser2ScimUser.mapToScimUser(updatedUserObj,getScimBaseUri());
    }

    @Override
    public ScimUser patchupdateUser(String token, String userId, PatchOp patchOp) {
        UserPatchBuilder patchBuilder = new UserPatchBuilder();
        for (Operation operation : patchOp.getOperations()) {
            patchBuilder.applyOperation(operation);
        }
        JsonPatch jsonPatch = patchBuilder.build();
        User patchedUser = userService.modify(token, userId, jsonPatch);
        return FRUser2ScimUser.mapToScimUser(patchedUser, getScimBaseUri());
    }


}

