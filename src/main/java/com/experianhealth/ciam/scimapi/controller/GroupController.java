package com.experianhealth.ciam.scimapi.controller;


import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimGroup;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.service.GroupService;
import com.experianhealth.ciam.scimapi.utils.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URI;
import java.util.Optional;


@RestController
@RequestMapping(ScimEndpoints.GROUPS)
public class GroupController {
    @Autowired
    private GroupService groupService;

    @GetMapping
    public ResponseEntity<ScimListResponse<ScimGroup>> getGroups(@RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken) {

        return ResponseEntity.ok(groupService.getGroups(AuthorizationUtils.validateBearerToken(bearerToken)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ScimGroup> getGroupById(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable String id) {

        Optional<ScimGroup> scimGroup = groupService.getGroupById(AuthorizationUtils.validateBearerToken(bearerToken), id);
        if (scimGroup.isEmpty()) {
            throw new CIAMNotFoundException("/scim/v2/Groups/" + id, "Group not found");
        }
        return ResponseEntity.ok(scimGroup.get());
    }

    @PostMapping
    public ResponseEntity<ScimGroup> createGroup(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @RequestBody ScimGroup requestedGroup
    ) {
        ScimGroup createdGroup = groupService.createGroup(AuthorizationUtils.validateBearerToken(bearerToken), requestedGroup);
        return ResponseEntity.created(URI.create(ScimEndpoints.GROUPS + "/" + createdGroup.getId())).body(createdGroup);
    }

    @DeleteMapping("/{groupId}")
    public ResponseEntity deleteGroup(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable("groupId") String groupId
    ) {
        Optional<ScimGroup> deletedGroup = groupService.deleteGroup(AuthorizationUtils.validateBearerToken(bearerToken), groupId);
        if (deletedGroup.isEmpty()) {
            throw new CIAMNotFoundException(ScimEndpoints.GROUPS + "/" + groupId, "Group not found");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping("/{groupId}")
    public ResponseEntity<ScimGroup> updateGroup(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable("groupId") String groupId,
            @RequestBody ScimGroup groupToUpdate
    ) {
        ScimGroup updatedGroup = groupService.updateGroup(AuthorizationUtils.validateBearerToken(bearerToken), groupId, groupToUpdate);
        return ResponseEntity.ok(updatedGroup);
    }
    
    @PatchMapping("/{groupId}")
    public ResponseEntity<ScimGroup> patchUpdateGroup(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable("groupId") String groupId,
            @RequestBody PatchOp patchOp
    ) {
        ScimGroup updatedGroup = groupService.patchUpdateGroup(AuthorizationUtils.validateBearerToken(bearerToken), groupId, patchOp);
        return ResponseEntity.ok(updatedGroup);
    }
}
