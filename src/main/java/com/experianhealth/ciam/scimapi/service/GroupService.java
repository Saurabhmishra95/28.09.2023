package com.experianhealth.ciam.scimapi.service;

import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimGroup;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;

import java.util.Optional;

public interface GroupService {
    ScimListResponse<ScimGroup> getGroups(String bearerToken);

    Optional<ScimGroup> getGroupById(String bearerToken, String id);

    ScimGroup createGroup(String token, ScimGroup group);

    Optional<ScimGroup> deleteGroup(String token, String groupId);

    ScimGroup updateGroup(String token, String groupId, ScimGroup updatedGroup);
    
    ScimGroup patchUpdateGroup(String token, String groupId, PatchOp patchOp);

}
