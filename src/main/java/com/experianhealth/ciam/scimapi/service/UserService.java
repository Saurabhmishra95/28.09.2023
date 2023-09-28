package com.experianhealth.ciam.scimapi.service;


import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.entity.ScimUser;

import java.util.Optional;

public interface UserService {
    ScimListResponse<ScimUser> getAllUsers(String token);

    Optional<ScimUser> getUserById(String token, String userId);

    ScimUser createUser(String token, ScimUser userToCreate);

    Optional<ScimUser> deleteUser(String token, String userId);

    ScimUser updateUser(String token, String userId, ScimUser updatedUser);

    ScimUser patchupdateUser(String token, String userId, PatchOp patchOp);


}
