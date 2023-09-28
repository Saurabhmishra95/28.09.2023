package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.forgerock.model.User;
import org.springframework.stereotype.Service;

/**
 * Implementation for the ForgeRock Managed User API.
 */
@Service
public class ManagedUserServiceImpl extends AbstractForgeRockIDMServiceImpl<User> implements ManagedUserService {
    private static final String USERS_PATH = "/openidm/managed/user";
    ManagedUserServiceImpl() {
        super(User.class);
    }

    @Override
    String getBasePath() {
        return USERS_PATH;
    }
}
