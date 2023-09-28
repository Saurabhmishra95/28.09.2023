package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class ScimUser {
    private static final List<String> SCHEMA_URIS = Collections.singletonList("urn:ietf:params:scim:schemas:core:2.0:User");

    private List<String> schemas = SCHEMA_URIS;
    private String id;
    private String userName;
    private String displayName;
    private Name name;
    private Address address;
    private List<PhoneNumber> phoneNumbers;
    private boolean active;
    private String locale;
    private String timezone;
    private List<Email> emails;
    private List<ScimGroupReference> groups;
    private Meta meta;

    // Default constructor
    public ScimUser() {
    }

    // Parameterized constructor
    public ScimUser(String id, String userName,String displayName, Name name, Address address, String email,
                    List<PhoneNumber> phoneNumbers, boolean active, String locale, String timezone, List<Email> emails, List<ScimGroupReference> groups,Meta meta) {
        this.id = id;
        this.userName = userName;
        this.displayName=displayName;
        this.name = name;
        this.address = address;
        this.phoneNumbers = phoneNumbers;
        this.active = active;
        this.locale = locale;
        this.timezone = timezone;
        this.emails = emails;
        this.groups=groups;
        this.meta=meta;
    }

    // Getters and Setters

    /**
     * Get the ID of the SCIM user.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the ID of the SCIM user.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the username of the SCIM user.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Set the username of the SCIM user.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Get the name of the SCIM user.
     */
    public Name getName() {
        return name;
    }

    /**
     * Set the name of the SCIM user.
     */
    public void setName(Name name) {
        this.name = name;
    }

    /**
     * Get the address of the SCIM user.
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Set the address of the SCIM user.
     */
    public void setAddress(Address address) {
        this.address = address;
    }


    /**
     * Get the phone numbers of the SCIM user.
     */
    public List<PhoneNumber> getPhoneNumbers() {
        return phoneNumbers;
    }

    /**
     * Set the phone numbers of the SCIM user.
     */
    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        if (phoneNumbers != null && !phoneNumbers.isEmpty()) {
            this.phoneNumbers = phoneNumbers;
        } else {
            this.phoneNumbers = null;
        }
    }

    /**
     * Check if the SCIM user is active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the active status of the SCIM user.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Get the locale of the SCIM user.
     */
    public String getLocale() {
        return locale;
    }

    /**
     * Set the locale of the SCIM user.
     */
    public void setLocale(String locale) {
        this.locale = locale;
    }

    /**
     * Get the timezone of the SCIM user.
     */
    public String getTimezone() {
        return timezone;
    }

    /**
     * Set the timezone of the SCIM user.
     */
    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    /**
     * Get the emails of the SCIM user.
     */
    public List<Email> getEmails() {
        return emails;
    }

    /**
     * Set the emails of the SCIM user.
     */
    public void setEmails(List<Email> list) {
        this.emails = list;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }
    public List<ScimGroupReference> getGroups() {
        return groups;
    }

    public void setGroups(List<ScimGroupReference> groups) {
        this.groups = groups;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
}
