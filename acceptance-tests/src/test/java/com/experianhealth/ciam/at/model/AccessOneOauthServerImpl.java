package com.experianhealth.ciam.at.model;

import java.net.URL;

public class AccessOneOauthServerImpl extends APIServerImpl implements OauthServer {
    public static final String TokenPath = "/oauth2/access_token";
    public static final String StatusPath = "/";
    public static final String BaseUri = "https://ehfops.idmworks.net";

    public AccessOneOauthServerImpl(URL url){
        super(url);
    }
    @Override
    public String getBasePath() {
        return "/am";
    }

    @Override
    public String getTokenPath() {
        return TokenPath;
    }
    @Override
    public String getHealthCheckPath() {
        return StatusPath;
    }

    @Override
    public String getValidClientId() {
        return "scim-client";
    }

    @Override
    public String getValidClientSecret() {
        return "1Password";
    }

    @Override
    public String getValidAdminUsername() {
        return "michael.petras@experian.com";
    }

    @Override
    public String getValidAdminPassword() {
        return "!QAZ2wsx#EDC4rfv";
    }
}
