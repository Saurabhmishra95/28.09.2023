package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.scimapi.entity.Email;
import com.experianhealth.ciam.scimapi.entity.Name;
import com.experianhealth.ciam.scimapi.entity.PhoneNumber;
import com.experianhealth.ciam.scimapi.entity.ScimUser;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ScimUser2FRUserTest {

    public static final String EXPECTED_ID = "bogus-id";
    public static final String EXPECTED_FAMILYNAME = "McLastname";
    public static final String EXPECTED_GIVENNAME = "McFirstname";
    public static final String EXPECTED_EMAILADDRESS = "email@example.com";
    public static final String EXPECTED_PHONENUMBER = "1-555-123-4567";
    public static final String EXPECTED_USERNAME = "username@example.com";
    public static final String NODOMAIN_USERNAME = "username";

    @Test
    @DisplayName("Test that a ScimUser request that has all required data passes")
    public void testMapToUser_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);
        configureBasicEmail(mockScimUser);
        configureBasicPhone(mockScimUser);

        when(mockScimUser.getUserName()).thenReturn(EXPECTED_USERNAME);

        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);

        // Then
        assertEquals(EXPECTED_ID,user.get_id());
        assertEquals(EXPECTED_USERNAME, user.getUserName());
        assertEquals(EXPECTED_GIVENNAME, user.getGivenName());
        assertEquals(EXPECTED_FAMILYNAME, user.getSn());
        assertEquals(EXPECTED_EMAILADDRESS, user.getMail());
        assertEquals(EXPECTED_PHONENUMBER, user.getTelephoneNumber());
    }

    @Test
    @DisplayName("Tests that the users email domain is added to a username without a domain")
    public void testMissingUserNameDomainUsesWorkEmailDomain_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);
        configureBasicEmail(mockScimUser);
        when(mockScimUser.getUserName()).thenReturn(NODOMAIN_USERNAME);
        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_USERNAME, user.getUserName());
    }

    @Test
    @DisplayName("Tests that the userName defaults to the work email value when there is no userName")
    public void testMissingUserNameDefaultstToWorkEmailValue_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);
        configureBasicEmail(mockScimUser);
        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_EMAILADDRESS, user.getUserName());
    }

    @Test
    @DisplayName("Test that the work email is chosen over other types of emails when there are multiple emails")
    public void testWorkEmailChosen_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);

        List<Email> mockEmails = configureMockEmails(mockScimUser);
        addExpectedWorkEmail(mockEmails);
        addUnexpectedHomeEmail(mockEmails);
        addNoValueEmail(mockEmails);

        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_EMAILADDRESS, user.getMail());
    }

    @Test
    @DisplayName("Test that the home email is chosen if that is the only valid option")
    public void testHomeEmailChosen_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);

        List<Email> mockEmails = configureMockEmails(mockScimUser);
        addExpectedHomeEmail(mockEmails);
        addNoValueEmail(mockEmails);
        addAllNullsEmail(mockEmails);

        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_EMAILADDRESS, user.getMail());
    }
    @Test
    @DisplayName("Test that an email with no type is chosen if that is the only valid option")
    public void testNoTypeEmailChosen_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);

        List<Email> mockEmails = configureMockEmails(mockScimUser);
        addExpectedNoTypeEmail(mockEmails);

        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_EMAILADDRESS, user.getMail());
    }

    @Test
    @DisplayName("Tests that the users mobile phone is chose over others")
    public void testMobilePhoneChosenOverWork_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);
        configureBasicEmail(mockScimUser);
        List<PhoneNumber> mockNumbers = configureMockPhoneNumbers(mockScimUser);
        addExpectedMobilePhoneNumber(mockNumbers);
        addUnExpectedWorkPhoneNumber(mockNumbers);
        addInvalidPhoneNumber(mockNumbers);
        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_PHONENUMBER, user.getTelephoneNumber());
    }

    @Test
    @DisplayName("Tests that the users work phone is chosen if no others are available")
    public void testWorkPhoneChosen_Success() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);
        configureBasicEmail(mockScimUser);
        List<PhoneNumber> mockNumbers = configureMockPhoneNumbers(mockScimUser);

        addExpectedWorkPhoneNumber(mockNumbers);
        addInvalidPhoneNumber(mockNumbers);
        //When
        User user = ScimUser2FRUser.mapToUser(mockScimUser);
        // Then
        assertEquals(EXPECTED_PHONENUMBER, user.getTelephoneNumber());
    }

    @Test
    @DisplayName("Tests that an email is required when the emails array os missing")
    public void testMapToUser_MissingEmailThrowsException() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicName(mockScimUser);
        when(mockScimUser.getUserName()).thenReturn(EXPECTED_USERNAME);

        //When
        CIAMInvalidRequestException exception = assertThrows(CIAMInvalidRequestException.class, () -> {
            User user = ScimUser2FRUser.mapToUser(mockScimUser);
        });
        assertEquals(ScimUser2FRUser.MISSING_REQUIRED_FIELD_EMAILS, exception.getPublicMessage());
    }

    @Test
    @DisplayName("Tests that an email value is required")
    public void testMapToUser_MissingEmailValueThrowsException() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        when(mockScimUser.getUserName()).thenReturn(EXPECTED_USERNAME);
        List<Email> mockEmails = configureMockEmails(mockScimUser);
        addNoValueEmail(mockEmails);
        addAllNullsEmail(mockEmails);
        configureBasicName(mockScimUser);

        //When
        CIAMInvalidRequestException exception = assertThrows(CIAMInvalidRequestException.class, () -> {
            User user = ScimUser2FRUser.mapToUser(mockScimUser);
        });
        assertEquals(ScimUser2FRUser.MISSING_REQUIRED_FIELD_EMAILS_VALUE, exception.getPublicMessage());
    }

    @Test
    @DisplayName("Tests that the Name field is required")
    public void testMapToUser_MissingNameThrowsException() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicEmail(mockScimUser);
        when(mockScimUser.getUserName()).thenReturn(EXPECTED_USERNAME);

        //When
        CIAMInvalidRequestException exception = assertThrows(CIAMInvalidRequestException.class, () -> {
            User user = ScimUser2FRUser.mapToUser(mockScimUser);
        });
        assertEquals(ScimUser2FRUser.MISSING_REQUIRED_FIELD_NAME, exception.getPublicMessage());
    }

    @Test
    @DisplayName("Tests that the givenName field is required")
    public void testMapToUser_MissingGivenNameThrowsException() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicEmail(mockScimUser);
        configureBasicName(mockScimUser);
        when(mockScimUser.getName().getGivenName()).thenReturn(null);
        when(mockScimUser.getUserName()).thenReturn(EXPECTED_USERNAME);

        //When
        CIAMInvalidRequestException exception = assertThrows(CIAMInvalidRequestException.class, () -> {
            User user = ScimUser2FRUser.mapToUser(mockScimUser);
        });
        assertEquals(ScimUser2FRUser.MISSING_REQUIRED_FIELD_NAME_GIVEN_NAME, exception.getPublicMessage());
    }

    @Test
    @DisplayName("Tests that the familyName field is required")
    public void testMapToUser_MissingFamilyNameThrowsException() {
        // Given
        ScimUser mockScimUser= createEmptyMockScimUser();
        configureBasicEmail(mockScimUser);
        configureBasicName(mockScimUser);
        when(mockScimUser.getName().getFamilyName()).thenReturn(null);
        when(mockScimUser.getUserName()).thenReturn(EXPECTED_USERNAME);

        //When
        CIAMInvalidRequestException exception = assertThrows(CIAMInvalidRequestException.class, () -> {
            User user = ScimUser2FRUser.mapToUser(mockScimUser);
        });
        assertEquals(ScimUser2FRUser.MISSING_REQUIRED_FIELD_NAME_FAMILY_NAME, exception.getPublicMessage());
    }

    private static ScimUser createEmptyMockScimUser() {
        ScimUser mockScimUser = mock(ScimUser.class);
        when(mockScimUser.getId()).thenReturn("bogus-id");
        return mockScimUser;
    }
    private static void configureBasicName(ScimUser mockScimUser){
        Name mockName = mock(Name.class);
        when(mockName.getFamilyName()).thenReturn(EXPECTED_FAMILYNAME);
        when(mockName.getGivenName()).thenReturn(EXPECTED_GIVENNAME);
        when(mockScimUser.getName()).thenReturn(mockName);
    }
    private static List<Email> configureMockEmails(ScimUser mockScimUser) {
        List<Email> mockEmails = new ArrayList<>();
        when(mockScimUser.getEmails()).thenReturn(mockEmails);
        return mockEmails;
    }
    private static void addEmail(List<Email> mockEmailList, String type, String value){
        Email email = mock(Email.class);
        when(email.getType()).thenReturn(type);
        when(email.getValue()).thenReturn(value);
        mockEmailList.add(email);
    }
    private static void configureBasicEmail(ScimUser mockScimUser) {
        List<Email> mockEmails = configureMockEmails(mockScimUser);
        addEmail(mockEmails,Email.Type.work.name(),EXPECTED_EMAILADDRESS);
    }

    private static void addExpectedWorkEmail(List<Email> mockEmails){
        addEmail(mockEmails,Email.Type.work.name(),EXPECTED_EMAILADDRESS);
    }
    private static void addUnexpectedHomeEmail(List<Email> mockEmails){
        addEmail(mockEmails,Email.Type.home.name(),"unexpected-home@yahoo.com");
    }
    private static void addNoValueEmail(List<Email> mockEmails){
        addEmail(mockEmails,Email.Type.home.name(),null);
        addEmail(mockEmails,Email.Type.work.name(),null);
    }
    private static void addAllNullsEmail(List<Email> mockEmails){
        addEmail(mockEmails,null,null);
    }
    private static void addExpectedNoTypeEmail(List<Email> mockEmails){
        addEmail(mockEmails,null,EXPECTED_EMAILADDRESS);
    }

    private static void addExpectedHomeEmail(List<Email> mockEmails){
        addEmail(mockEmails,Email.Type.home.name(),EXPECTED_EMAILADDRESS);
    }
    private static List<PhoneNumber> configureMockPhoneNumbers(ScimUser mockScimUser) {
        List<PhoneNumber> mockPhoneNumbers = new ArrayList<>();
        when(mockScimUser.getPhoneNumbers()).thenReturn(mockPhoneNumbers);
        return mockPhoneNumbers;
    }
    private static void addPhoneNumber(List<PhoneNumber> mockPhoneNumbers, String type, String value){
        PhoneNumber phoneNumber = mock(PhoneNumber.class);
        when(phoneNumber.getType()).thenReturn(type);
        when(phoneNumber.getValue()).thenReturn(value);
        mockPhoneNumbers.add(phoneNumber);
    }
    private static void configureBasicPhone(ScimUser mockScimUser) {
        addExpectedMobilePhoneNumber(configureMockPhoneNumbers(mockScimUser));
    }

    private static void  addInvalidPhoneNumber(List<PhoneNumber> mockPhoneNumbers){
        addPhoneNumber(mockPhoneNumbers, PhoneNumber.Type.mobile.name(), null);
    }

    private static void addExpectedMobilePhoneNumber(List<PhoneNumber> mockPhoneNumbers){
        addPhoneNumber(mockPhoneNumbers, PhoneNumber.Type.mobile.name(),EXPECTED_PHONENUMBER);
    }

    private static void addExpectedWorkPhoneNumber(List<PhoneNumber> mockPhoneNumbers){
        addPhoneNumber(mockPhoneNumbers, PhoneNumber.Type.work.name(),EXPECTED_PHONENUMBER);
    }

    private static void addUnExpectedWorkPhoneNumber(List<PhoneNumber> mockPhoneNumbers){
        addPhoneNumber(mockPhoneNumbers, PhoneNumber.Type.work.name(),"unexpected-work-phonenumber");
    }

    private static void addExpectedPhoneNumber(List<PhoneNumber> mockPhoneNumbers){
        addPhoneNumber(mockPhoneNumbers, null,EXPECTED_PHONENUMBER);
    }

}
