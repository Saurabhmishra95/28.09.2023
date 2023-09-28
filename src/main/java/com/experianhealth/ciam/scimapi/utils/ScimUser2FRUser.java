package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.scimapi.entity.Address;
import com.experianhealth.ciam.scimapi.entity.Email;
import com.experianhealth.ciam.scimapi.entity.PhoneNumber;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Optional;

public class ScimUser2FRUser {

    public static final String MISSING_REQUIRED_FIELD_NAME = "Missing required field: name";
    public static final String MISSING_REQUIRED_FIELD_NAME_GIVEN_NAME = "Missing required field: name.givenName";
    public static final String MISSING_REQUIRED_FIELD_NAME_FAMILY_NAME = "Missing required field: name.familyName";
    public static final String MISSING_REQUIRED_FIELD_EMAILS = "Missing required field: emails";
    public static final String MISSING_REQUIRED_FIELD_EMAILS_VALUE = "Missing required field: emails[].value";

    /**
     * Converts the SCIMUserRequest payload to the ForgeRock schema UserRequest object.
     *
     * @param scimUser the SCIMUserRequest payload
     * @return a UserRequest object representing the user
     */
    public static User mapToUser(ScimUser scimUser) {
        User frUser = new User();
        frUser.set_id(scimUser.getId());
        String workEmail = determineWorkEmail(scimUser.getEmails());
        frUser.setMail(workEmail);
        frUser.setUserName(determineUserName(scimUser.getUserName(), workEmail));

        Optional<String> phoneNumber = determinePhoneNumber(scimUser);
        if(phoneNumber.isPresent()) {
            frUser.setTelephoneNumber(phoneNumber.get());
        }
        if (scimUser.getName() == null) {
            throw new CIAMInvalidRequestException(MISSING_REQUIRED_FIELD_NAME);
        }
        if (scimUser.getName().getGivenName() == null) {
            throw new CIAMInvalidRequestException(MISSING_REQUIRED_FIELD_NAME_GIVEN_NAME);
        }
        frUser.setGivenName(scimUser.getName().getGivenName());

        if (scimUser.getName().getFamilyName() == null) {
            throw new CIAMInvalidRequestException(MISSING_REQUIRED_FIELD_NAME_FAMILY_NAME);
        }
        frUser.setSn(scimUser.getName().getFamilyName());

        if (scimUser.getName().getFormatted() != null) {
            frUser.setDisplayName(scimUser.getName().getFormatted());
        }
        if (scimUser.getDisplayName() != null) {
            frUser.setDisplayName(scimUser.getDisplayName());
        }
        if (scimUser.getTimezone() != null) {
            frUser.setPreferredTimezone(scimUser.getTimezone());
        }
        mapAddress(scimUser.getAddress(), frUser);

        frUser.setAccountStatus("ACTIVE");

        return frUser;
    }

    private static void mapAddress(Address scimAddress, User frUser) {
        if (scimAddress != null) {
            frUser.setStateProvince((scimAddress.getRegion()));;
            frUser.setPostalCode(scimAddress.getPostalCode());
            frUser.setCountry(scimAddress.getCountry());
        }
    }

    static String determineUserName(String userName, String workEmail) {
        Optional<String> emaildomain = Optional.empty();

        String[] emailparts = StringUtils.split(workEmail, '@');
        if (emailparts.length > 1) {
            emaildomain = Optional.ofNullable(emailparts[1]);
        }

        if (userName != null) {
            String[] parts = StringUtils.split(userName, '@');
            if (!userName.contains("@")) {
                userName = userName + "@" + emaildomain.get();
            }
        } else {
            userName = workEmail;
        }
        return userName.toLowerCase();
    }
    static String determineWorkEmail(List<Email> emails) {
        String workEmail = null;
        if(emails == null || emails.isEmpty()){
            throw new CIAMInvalidRequestException(MISSING_REQUIRED_FIELD_EMAILS);
        }
        emails = filterValidEmails(emails);
        if(emails.isEmpty()){
            throw new CIAMInvalidRequestException(MISSING_REQUIRED_FIELD_EMAILS_VALUE);
        }
        Optional<Email> workEmailOptional = locateEmailType(emails, Email.Type.work.name());
        if(workEmailOptional.isPresent()) {
            workEmail = workEmailOptional.get().getValue();
        }else {
            workEmail = emails.get(0).getValue();
        }
        return workEmail.toLowerCase();
    }
    static List<Email> filterValidEmails(List<Email> emails){
        return emails.stream().filter(email -> StringUtils.isNotEmpty(email.getValue())).toList();
    }
    static Optional<Email> locateEmailType(List<Email> emails, String type){
        return emails.stream()
                .filter(email -> StringUtils.equalsIgnoreCase(type, email.getType()) && StringUtils.isNotEmpty(email.getValue()))
                .findFirst();
    }
    static Optional<String> determinePhoneNumber(ScimUser scimUser){
        if(scimUser.getPhoneNumbers() == null || scimUser.getPhoneNumbers().isEmpty()){
            return Optional.empty();
        }
        List<PhoneNumber> phoneNumbers = filterValidPhoneNumbers(scimUser.getPhoneNumbers());
        if(phoneNumbers.isEmpty()) return Optional.empty();

        Optional<PhoneNumber> number = locatePhoneType(phoneNumbers, PhoneNumber.Type.mobile.name());
        if(number.isPresent()) return Optional.of(number.get().getValue());

        number = locatePhoneType(phoneNumbers, PhoneNumber.Type.work.name());
        if(number.isPresent()) return Optional.of(number.get().getValue());

        return Optional.of(phoneNumbers.get(0).getValue());
    }
    static Optional<PhoneNumber> locatePhoneType(List<PhoneNumber> phoneNumbers, String type){
        return phoneNumbers.stream()
                .filter(phoneNumber -> StringUtils.equalsIgnoreCase(type, phoneNumber.getType()) && StringUtils.isNotEmpty(phoneNumber.getValue()))
                .findFirst();
    }
    static List<PhoneNumber> filterValidPhoneNumbers(List<PhoneNumber> phoneNumbers){
        return phoneNumbers.stream().filter(phoneNumber -> StringUtils.isNotEmpty(phoneNumber.getValue())).toList();
    }




}
