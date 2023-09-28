package com.experianhealth.ciam.at.model;

import java.net.URL;
import java.util.StringJoiner;

public class APIServerImpl implements APIServer {
    final String baseUri;
    final int port;

    public APIServerImpl(String baseUri, int port) {
        this.baseUri = baseUri;
        this.port = port;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", APIServerImpl.class.getSimpleName() + "[", "]")
                .add("baseUri='" + baseUri + "'")
                .add("port=" + port)
                .toString();
    }

    public APIServerImpl(URL url) {
        this(String.format("%s://%s", url.getProtocol(), url.getHost()), url.getPort());
    }
    public String getBaseUri() {
        return baseUri;
    }

    public String getBasePath() {
        return "/";
    }

    @Override
    public String getHealthCheckPath() {
        return "/";
    }

    @Override
    public int getPort() {
        return port;
    }
}
