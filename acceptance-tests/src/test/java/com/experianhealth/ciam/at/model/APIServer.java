package com.experianhealth.ciam.at.model;

public interface APIServer {
    String getBaseUri();

    String getBasePath();

    String getHealthCheckPath();

    int getPort();
}
