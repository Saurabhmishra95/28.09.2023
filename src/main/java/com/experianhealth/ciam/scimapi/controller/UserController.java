package com.experianhealth.ciam.scimapi.controller;

import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.exception.CIAMUnauthorizedException;
import com.experianhealth.ciam.scimapi.entity.PatchOp;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import com.experianhealth.ciam.scimapi.service.UserService;
import com.experianhealth.ciam.scimapi.utils.AuthorizationUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;


@RestController
@RequestMapping(ScimEndpoints.USERS)
public class UserController {

    public static final String USERS_PATH = ScimEndpoints.USERS + "/";
    @Autowired
    private UserService userService;

    @GetMapping
    public ScimListResponse<ScimUser> getUsers(@RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken) {

        return userService.getAllUsers(AuthorizationUtils.validateBearerToken(bearerToken));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ScimUser> getUserById(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable String userId) {

        Optional<ScimUser> user = userService.getUserById(AuthorizationUtils.validateBearerToken(bearerToken), userId);
        if(user.isEmpty()){
            throw new CIAMNotFoundException(USERS_PATH + userId, "User not found");
        }
        return ResponseEntity.ok(user.get());
    }

    @PostMapping
    public ResponseEntity<ScimUser> createUser(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @RequestBody ScimUser requestedUser
    ) {

        ScimUser createdUser = userService.createUser(AuthorizationUtils.validateBearerToken(bearerToken), requestedUser);
        return ResponseEntity.created(URI.create(USERS_PATH + createdUser.getId())).body(createdUser);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity deleteUser(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable("userId") String userId
    ) {

        Optional<ScimUser> deleteUser = userService.deleteUser(AuthorizationUtils.validateBearerToken(bearerToken), userId);
        if(deleteUser.isEmpty()){
            throw new CIAMNotFoundException(USERS_PATH + userId, "User not found");
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ScimUser> updateUser(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable("userId") String userId,
            @RequestBody ScimUser updatedUser
    ) {
        ScimUser user = userService.updateUser(AuthorizationUtils.validateBearerToken(bearerToken), userId, updatedUser);
        return ResponseEntity.ok(user);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ScimUser> patchupdateUser(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken,
            @PathVariable String userId,
            @RequestBody PatchOp patchOp
    ) {
        ScimUser updatedScimUser = userService.patchupdateUser(AuthorizationUtils.validateBearerToken(bearerToken), userId, patchOp);
        return ResponseEntity.ok(updatedScimUser);
    }
}
