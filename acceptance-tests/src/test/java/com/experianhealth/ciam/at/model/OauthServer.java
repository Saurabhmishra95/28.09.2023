package com.experianhealth.ciam.at.model;

public interface OauthServer extends APIServer {

    public String getTokenPath();

    String getValidClientId();

    String getValidClientSecret();

    String getValidAdminUsername();

    String getValidAdminPassword();
}
