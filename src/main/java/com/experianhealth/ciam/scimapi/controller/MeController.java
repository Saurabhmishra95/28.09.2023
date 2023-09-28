package com.experianhealth.ciam.scimapi.controller;



import com.experianhealth.ciam.scimapi.entity.ScimUser;
import com.experianhealth.ciam.scimapi.service.MeService;
import com.experianhealth.ciam.scimapi.utils.AuthorizationUtils;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping(ScimEndpoints.ME)
public class MeController {

	@Autowired
    private MeService meService;

	public static final String ME_PATH = ScimEndpoints.ME ;

    @GetMapping()
    public ResponseEntity<ScimUser> getMe(
            @RequestHeader(value = "Authorization", required = false) Optional<String> bearerToken) {
        
        String token = AuthorizationUtils.validateBearerToken(bearerToken);
        ScimUser scimUser = meService.getMe(token);

        if (scimUser == null) {
            throw new CIAMNotFoundException(ME_PATH, "User not found");
        }
        return ResponseEntity.ok(scimUser);
    }


}
