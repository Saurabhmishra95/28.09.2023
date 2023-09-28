package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.forgerock.model.EffectiveRole;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.scimapi.controller.ScimEndpoints;
import com.experianhealth.ciam.scimapi.entity.*;

import java.util.ArrayList;
import java.util.List;

public class FRUser2ScimUser {

    /**
     * Maps the User objects to ScimUser objects.
     */
    public static List<ScimUser> mapToScimUsers(List<User> users, String scimBaseUri) {
        List<ScimUser> scimUsers = new ArrayList<>();

        // Iterate over each User object in the list
        for (User user : users) {
            ScimUser scimUser = mapToScimUser(user,scimBaseUri);
            // Add the ScimUser object to the list if any attributes are set
            if (scimUser.getId() != null || scimUser.getUserName() != null || scimUser.getDisplayName() != null || scimUser.getName() != null ||
                    scimUser.getEmails() != null ||
                    scimUser.getPhoneNumbers() != null ||
                    scimUser.getAddress() != null ||
                    scimUser.getGroups() != null ||
                    scimUser.getMeta() != null
            )
            {
                scimUsers.add(scimUser);
            }
        }
        return scimUsers;
    }

    public static ScimUser mapToScimUser(User user, String scimBaseUri) {
        ScimUser scimUser = new ScimUser();
        scimUser.setId(user.get_id());
        scimUser.setUserName(user.getUserName());
        scimUser.setDisplayName(user.getDisplayName());
        scimUser.setName(createNameFromFRUser(user));
        scimUser.setActive(mapAccountStatus(user));
        scimUser.setEmails(createEmailsFromFRUser(user));
        scimUser.setTimezone(user.getPreferredTimezone());
        scimUser.setPhoneNumbers(createPhoneNumberFromFRUser(user));
        scimUser.setAddress(createAddressFromFRUser(user));
        scimUser.setGroups(createRoles(user,scimBaseUri));
        scimUser.setMeta(createMeta(user,scimBaseUri));
        return scimUser;
    }


    /**
     * Creates a Name object based on the User object's attributes.
     */
    static Name createNameFromFRUser(User user) {
        if (user.getGivenName() != null || user.getSn() != null || user.getDisplayName() != null) {
            Name name = new Name();
            name.setGivenName(user.getGivenName());
            name.setFamilyName(user.getSn());
            name.setFormatted(user.getDisplayName());
            return name;
        }
        return null;
    }

    /**
     * Creates an Email object based on the User object's attributes.
     */
    static List<Email> createEmailsFromFRUser(User user) {
        List<Email> emails = new ArrayList<>();

        if (user.getMail() != null && !user.getMail().isEmpty()) {
            Email workEmail = new Email();
            workEmail.setValue(user.getMail());
            workEmail.setType(Email.Type.work.name());
            workEmail.setPrimary(true);
            emails.add(workEmail);
        }
        return emails.isEmpty() ? null : emails;
    }

    /**
     * Creates a list of PhoneNumber objects based on the User object's attributes.
     */
    static List<PhoneNumber> createPhoneNumberFromFRUser(User user) {
        if (user.getTelephoneNumber() != null && !user.getTelephoneNumber().isEmpty()) {
            List<PhoneNumber> phoneNumbers = new ArrayList<>();
            PhoneNumber phoneNumber = new PhoneNumber();
            phoneNumber.setValue(user.getTelephoneNumber());
            phoneNumber.setType(Email.Type.work.name());
            phoneNumbers.add(phoneNumber);
            return phoneNumbers;
        }
        return null;
    }

    /**
     * Creates an Address object based on the User object's attributes.
     */
    static Address createAddressFromFRUser(User user) {
        if (user.getPostalAddress() != null || user.getStateProvince() != null ||
                user.getPostalCode() != null || user.getCountry() != null) {
            Address address = new Address();
            address.setFormatted(formatAddress(user));
            address.setLocality(user.getPostalAddress());
            address.setRegion(user.getStateProvince());
            address.setPostalCode(user.getPostalCode());
            address.setCountry(user.getCountry());
            address.setPrimary(true);
            return address;
        }
        return null;
    }

    /**
     * Formats the address components into a single formatted address string.
     */
    static String formatAddress(User user) {
        StringBuilder formattedAddress = new StringBuilder();
        if (user.getPostalAddress() != null) {
            formattedAddress.append(user.getPostalAddress());
            formattedAddress.append(", ");
        }
        if (user.getStateProvince() != null) {
            formattedAddress.append(user.getStateProvince());
            formattedAddress.append(", ");
        }
        if (user.getPostalCode() != null) {
            formattedAddress.append(user.getPostalCode());
            formattedAddress.append(", ");
        }
        if (user.getCountry() != null) {
            formattedAddress.append(user.getCountry());
        }
        return formattedAddress.toString();
    }

    /**
     * Creates a list of SCIM Group references based on the User's effective roles.
     *
     * @param user The User object representing the user from the IDM service.
     * @return A list of ScimGroupReference objects, or null if the user has no effective roles.
     */
    static List<ScimGroupReference> createRoles(User user, String scimBaseUri) {
        if (user.getEffectiveRoles() != null && !user.getEffectiveRoles().isEmpty()) {
            List<ScimGroupReference> groups = new ArrayList<>();
            for (EffectiveRole role : user.getEffectiveRoles()) {
                ScimGroupReference scimGroup = new ScimGroupReference();
                scimGroup.setValue(role.get_refResourceId());
                scimGroup.set$ref(scimBaseUri + "/Groups/" + role.get_refResourceId());
                groups.add(scimGroup);
            }
            return groups;
        }
        return null;
    }

    static Meta createMeta(User user, String scimBaseUri) {
        Meta meta = new Meta();
        meta.setResourceType("User");
        meta.setVersion(user.get_rev());
        String userLocation = scimBaseUri + ScimEndpoints.USERS + "/" + user.get_id();
        meta.setLocation(userLocation);
        return meta;
    }

    static boolean mapAccountStatus(User user) {
        String accountStatus = user.getAccountStatus();
        return "active".equalsIgnoreCase(accountStatus);
    }


}
