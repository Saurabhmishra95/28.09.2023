package com.experianhealth.ciam.at.model;

public class CHIPOauthServerImpl implements OauthServer {
    public static final String CHIPTokenPath = "/api/token";
    public static final String CHIPStatusPath = "/SessionCheck";
    public static final String CHIPBaseUri = "https://dev.chip.experianhealth.com";

    @Override
    public String getBaseUri() {
        return CHIPBaseUri;
    }

    @Override
    public String getBasePath() {
        return "/";
    }

    @Override
    public String getTokenPath() {
        return CHIPTokenPath;
    }
    @Override
    public String getHealthCheckPath() {
        return CHIPStatusPath;
    }

    @Override
    public String getValidClientId() {
        return "imOVVXrbH03Qfg0zqhpK";
    }
    @Override
    public String getValidClientSecret() {
        return "NU8uq10MvqQ8+zJ6JqZ3";
    }

    @Override
    public String getValidAdminUsername() {
        return "username";
    }

    @Override
    public String getValidAdminPassword() {
        return "password";
    }

    @Override
    public int getPort() {
        return 443;
    }


}
