package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Address {
    private String locality;


    public Address(String locality, String region, String postalCode, String country, String formatted,
                   boolean primary) {
        super();
        this.locality = locality;
        this.region = region;
        this.postalCode = postalCode;
        this.country = country;
        this.formatted = formatted;
        this.primary = primary;
    }

    private String region;
    private String postalCode;

    public Address() {

    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getFormatted() {
        return formatted;
    }

    public void setFormatted(String formatted) {
        this.formatted = formatted;
    }

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    private String country;
    private String formatted;
    private boolean primary;


}