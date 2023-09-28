package com.experianhealth.ciam.at.model;

import java.net.URL;

public class AccessOneScimServerImpl extends APIServerImpl implements ScimServer {

    public AccessOneScimServerImpl(URL url){
        super(url);
    }
    @Override
    public String getHealthCheckPath() {
        return "/status";
    }
}
