package com.experianhealth.ciam.portal.entity;

public class PortalConfiguration {
    String amBaseUrl;
    String idmBaseUrl;
    String clientId;

    public String getAmBaseUrl() {
        return amBaseUrl;
    }

    public void setAmBaseUrl(String amBaseUrl) {
        this.amBaseUrl = amBaseUrl;
    }

    public String getIdmBaseUrl() {
        return idmBaseUrl;
    }

    public void setIdmBaseUrl(String idmBaseUrl) {
        this.idmBaseUrl = idmBaseUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
}
