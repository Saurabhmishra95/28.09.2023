package com.experianhealth.ciam.at.model;

import java.net.MalformedURLException;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isEmpty;

public class APIServerFactory {
    public static final String ACCESS_ONE = "AccessOne";
    public static final String CHIP = "CHIP";

    public static ScimServer createScimServer(String providerName) {


        switch( providerName) {

            case ACCESS_ONE: {
                return new AccessOneScimServerImpl(readEnvironment("SCIM_BASEURL"));
            }
            default: {
                throw new IllegalArgumentException("Unrecognized scim server: " + providerName);
            }
        }
    }
    public static OauthServer createOauthServer(String providerName){
        switch( providerName) {
            case CHIP: {
                return new CHIPOauthServerImpl();
            }
            case ACCESS_ONE: {
                return new AccessOneOauthServerImpl(readEnvironment("AM_BASEURL"));
            }
            default: {
                throw new IllegalArgumentException("Unrecognized token provider: " + providerName);
            }
        }
    }

    static URL readEnvironment(String envVarName) {
        String baseUrl = System.getenv(envVarName);
        try {
            if( isEmpty(baseUrl)){
                String errorMessage = "Missing required environment variable: " + envVarName;
                System.err.println(errorMessage);
                throw new RuntimeException(errorMessage);
            }
            System.out.println("Environment Setting: " + envVarName + "=" + baseUrl);
            return new URL(baseUrl);

        }catch(MalformedURLException e){
            throw new RuntimeException("Error parsing: " +  envVarName + "=" + baseUrl,e);
        }
    }
}
