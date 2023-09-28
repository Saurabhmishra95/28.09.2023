package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.forgerock.model.User;

public interface ForgeRockAMService {
    /**
     * Retrieves user information from the ForgeRock AM userinfo endpoint.
     *
     * @param token The access token used for authentication.
     * @return UserInfo object containing user information.
     */
    User getUserInfo(String token);

    /**
     * Obtains an access token using the client credentials (client ID and secret),
     * username, and password through the password grant flow.
     *
     * @param clientID     The client ID of the registered OAuth 2.0 client.
     * @param secret       The client secret of the registered OAuth 2.0 client.
     * @param userName     The username of the user.
     * @param password     The password of the user.
     * @return The obtained access token.
     */
    String getAccessToken(String clientID, String secret, String userName, String password);


}
