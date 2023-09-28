package com.experianhealth.ciam.scimapi.utils;

import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.scimapi.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.json.JsonPatch;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserPatchBuilderTest {

    private UserPatchBuilder builder;

    @BeforeEach
    void setUp() {
        builder = new UserPatchBuilder();
    }

    @Test
    void testAddAttribute() {
        builder.addAttribute("/givenName", "John");
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testReplaceAttribute() {
        builder.replaceAttribute("/givenName", "Doe");
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testRemoveAttribute() {
        builder.removeAttribute("/givenName");
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testApplyOperationForName() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("name");
        Map<String, Object> nameMap = new HashMap<>();
        nameMap.put("givenName", "John");
        nameMap.put("familyName", "Doe");
        operation.setValue(nameMap);

        builder.applyOperation(operation);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testApplyOperationForEmails() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("emails");
        
        Email email = new Email();
        email.setValue("john.doe@example.com");
        
        operation.setValue(Arrays.asList(email));

        builder.applyOperation(operation);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testUnsupportedOperation() {
        Operation operation = new Operation();
        operation.setOp("unsupportedOp");
        operation.setPath("name");
        operation.setValue("John");

        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation));
    }

    @Test
    void testApplyOperationForPhoneNumbers() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("phoneNumbers");
        
        PhoneNumber phoneNumber = new PhoneNumber();
        phoneNumber.setValue("123-456-7890");
        
        operation.setValue(Arrays.asList(phoneNumber));

        builder.applyOperation(operation);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testApplyOperationForAddresses() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("addresses");
        
        Map<String, Object> addressMap = new HashMap<>();
        addressMap.put("locality", "City");
        addressMap.put("region", "State");
        addressMap.put("postalCode", "12345");
        addressMap.put("country", "Country");
        
        operation.setValue(addressMap);

        builder.applyOperation(operation);
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }

    @Test
    void testApplyOperationForUnsupportedNameAttributePath() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("name.unsupported");
        operation.setValue("Value");

        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation));
    }

    @Test
    void testApplyOperationForUnsupportedGenericAttributePath() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("unsupportedPath");
        operation.setValue("Value");

        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation));
    }

    @Test
    void testApplyOperationForUnsupportedAddressSubAttribute() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("addresses.unsupported");
        operation.setValue("Value");

        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation));
    }

    @Test
    void testApplyOperationForInvalidValueType() {
        Operation operation = new Operation();
        operation.setOp("add");
        operation.setPath("addresses");
        operation.setValue("InvalidValue");

        assertThrows(CIAMInvalidRequestException.class, () -> builder.applyOperation(operation));
    }

    @Test
    void testBuildWithNoOperations() {
        JsonPatch patch = builder.build();
        assertNotNull(patch);
    }
}
