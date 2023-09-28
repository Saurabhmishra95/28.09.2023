package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PhoneNumber {
    private String value;
    private String type;

    // Constructors, getters, and setters

    public PhoneNumber() {
    }

    public PhoneNumber(String value, String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public static enum Type {
        mobile,
        work,
        home,
        other,
        fax
    }
}
