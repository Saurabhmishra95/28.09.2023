package com.experianhealth.ciam.scimapi.service.impl;


import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.forgerock.service.ForgeRockAMService;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import com.experianhealth.ciam.scimapi.service.MeService;
import com.experianhealth.ciam.scimapi.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MeServiceImpl implements MeService {

    @Autowired
    private ForgeRockAMService forgeRockAMService;

    @Autowired
    private UserService userService;

    @Autowired
    private RequestContextProvider contextProvider;

    @Override
    public ScimUser getMe(String token) {
        User user = forgeRockAMService.getUserInfo(token);
        return userService.getUserById(token,user.get_id()).get();
    }
}
