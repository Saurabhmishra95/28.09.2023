package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.exception.CIAMUnauthorizedException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthorizationUtils {
    private static final String BEARER_PREFIX = "Bearer ";

    public static String validateBearerToken(Optional<String> authorizationHeader) {
        if (!authorizationHeader.isPresent()) {
            throw new CIAMUnauthorizedException("Authorization header is missing");
        }
        return extractBearerToken(authorizationHeader.get());
    }
    public static void validateAuthorizationHeader(String authorizationHeader) {
        if (StringUtils.isEmpty(authorizationHeader)) {
            throw new CIAMUnauthorizedException("Authorization failure. The authorization header is invalid or missing");
        }
    }

    /**
     * Extracts the bearer token from the authorization header.
     * Validates the authorization header format.
     *
     * @param authorizationHeader The authorization header containing the bearer token.
     * @return The extracted bearer token.
     */
    public static String extractBearerToken(String authorizationHeader) {
        validateAuthorizationHeader(authorizationHeader);
        String bearerToken = authorizationHeader.startsWith(BEARER_PREFIX)
                ? authorizationHeader.substring(BEARER_PREFIX.length()).trim()
                : authorizationHeader;

        if (StringUtils.isEmpty(bearerToken)) {
            throw new CIAMUnauthorizedException("Missing Bearer Token");
        }
        return bearerToken;
    }
}
