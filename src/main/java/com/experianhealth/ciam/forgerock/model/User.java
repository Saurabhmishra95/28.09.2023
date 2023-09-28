package com.experianhealth.ciam.forgerock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.StringJoiner;

/**
 * Represents a User entity.
 */

/**
 * {
 *     "_id": "78b3ae21-8965-47dd-9c8c-da948ad4d47a",
 *     "_rev": "04758c01-8032-4804-897a-449186f9a3c0-300812",
 *     "country": null,
 *     "telephoneNumber": null,
 *     "mail": "chetan.bissa@idmworks.com",
 *     "memberOfOrgIDs": [],
 *     "city": null,
 *     "displayName": null,
 *     "effectiveAssignments": [],
 *     "postalCode": null,
 *     "description": null,
 *     "effectiveApplications": [],
 *     "profileImage": null,
 *     "expireAccount": null,
 *     "accountStatus": "active",
 *     "aliasList": [],
 *     "inactiveDate": null,
 *     "activeDate": null,
 *     "consentedMappings": [],
 *     "sn": "Bissa",
 *     "effectiveGroups": [],
 *     "preferences": {
 *         "marketing": false,
 *         "updates": false
 *     },
 *     "organizationName": null,
 *     "givenName": "Chetan",
 *     "stateProvince": null,
 *     "userName": "chetanbissa27",
 *     "postalAddress": null,
 *     "effectiveRoles": [],
 *     "activateAccount": null
 * }
 */

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    private String _id;
    private String sn;
    private String userName;
    private String givenName;
    private String displayName;
    private String postalAddress;
    private String mail;
    private String telephoneNumber;
    private String stateProvince;
    private String postalCode;
    private String country;
    private List<EffectiveRole> effectiveRoles;
    private List<Application> effectiveApplications;
    private String _rev;
    private String accountStatus;
    private String preferredTimezone;

    // Getters and Setters for all the attributes including the new effectiveApplications
    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getPostalAddress() {
        return postalAddress;
    }

    public void setPostalAddress(String postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getTelephoneNumber() {
        return telephoneNumber;
    }

    public void setTelephoneNumber(String telephoneNumber) {
        this.telephoneNumber = telephoneNumber;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
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

    public List<EffectiveRole> getEffectiveRoles() {
        return effectiveRoles;
    }

    public void setEffectiveRoles(List<EffectiveRole> effectiveRoles) {
        this.effectiveRoles = effectiveRoles;
    }

    public List<Application> getEffectiveApplications() {
        return effectiveApplications;
    }

    public void setEffectiveApplications(List<Application> effectiveApplications) {
        this.effectiveApplications = effectiveApplications;
    }

    public String get_rev() {
        return _rev;
    }

    public void set_rev(String _rev) {
        this._rev = _rev;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getPreferredTimezone() {
        return preferredTimezone;
    }

    public void setPreferredTimezone(String preferredTimezone) {
        this.preferredTimezone = preferredTimezone;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", User.class.getSimpleName() + "[", "]")
                .add("_id='" + _id + "'")
                .add("sn='" + sn + "'")
                .add("userName='" + userName + "'")
                .add("givenName='" + givenName + "'")
                .add("displayName='" + displayName + "'")
                .add("postalAddress='" + postalAddress + "'")
                .add("mail='" + mail + "'")
                .add("telephoneNumber='" + telephoneNumber + "'")
                .add("stateProvince='" + stateProvince + "'")
                .add("postalCode='" + postalCode + "'")
                .add("country='" + country + "'")
                .add("effectiveRoles=" + effectiveRoles)
                .add("effectiveApplications=" + effectiveApplications)
                .add("_rev='" + _rev + "'")
                .add("accountStatus='" + accountStatus + "'")
                .add("preferredTimezone='" + preferredTimezone + "'")  // Added this line
                .toString();
    }

}
