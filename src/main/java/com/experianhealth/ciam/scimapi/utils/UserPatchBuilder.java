package com.experianhealth.ciam.scimapi.utils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPatch;

import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.scimapi.entity.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class UserPatchBuilder {

    private static final String ADD = "add";
    private static final String REPLACE = "replace";
    private static final String REMOVE = "remove";

    private final JsonArrayBuilder operations = Json.createArrayBuilder();
    private final ObjectMapper mapper = new ObjectMapper();

    private UserPatchBuilder applyAttributeOperation(String op, String path, Object value) {
        if (value != null || REMOVE.equalsIgnoreCase(op)) {
            JsonObjectBuilder operationBuilder = Json.createObjectBuilder()
                    .add("op", op)
                    .add("path", path);
            if (value != null) {
                operationBuilder.add("value", value.toString());
            }
            operations.add(operationBuilder.build());
        }
        return this;
    }

    public UserPatchBuilder addAttribute(String path, Object value) {
        return applyAttributeOperation(ADD, path, value);
    }

    public UserPatchBuilder replaceAttribute(String path, Object value) {
        return applyAttributeOperation(REPLACE, path, value);
    }

    public UserPatchBuilder removeAttribute(String path) {
        return applyAttributeOperation(REMOVE, path, null);
    }

    public void applyOperation(Operation operation) {
        String op = operation.getOp().toLowerCase();
        switch(op) {
            case ADD:
            case REPLACE:
            case REMOVE:
                handleAttributeOperation(op, operation);
                break;
            default:
                throw new CIAMInvalidRequestException("Unsupported operation: " + op);
        }
    }

    private void handleAttributeOperation(String op, Operation operation) {
        String path = operation.getPath();
        Object value = operation.getValue();

        if (isNameAttribute(path)) {
            handleNameOperation(op, path, value);
        } else if ("emails".equals(path)) {
            handleEmailOperation(op, value);
        } else if ("phoneNumbers".equals(path)) {
            handlePhoneNumberOperation(op, value);
        } else if (path.startsWith("addresses")) {
            handleAddressOperation(op, path, value);
        } else{
            handleGenericOperation(op, path, value);
        }
    }

    private boolean isNameAttribute(String path) {
        return path.equalsIgnoreCase("name") || path.startsWith("name.");
    }

    private void handleNameOperation(String op, String path, Object value) {
        if (path.equalsIgnoreCase("name") && value instanceof Map) {
            Map<String, Object> nameMap = (Map<String, Object>) value;
            applyAttributeOperation(op, "/givenName", nameMap.get("givenName"));
            applyAttributeOperation(op, "/sn", nameMap.get("familyName"));
        } else if ("name.givenName".equals(path)) {
            applyAttributeOperation(op, "/givenName", value);
        } else if ("name.familyName".equals(path)) {
            applyAttributeOperation(op, "/sn", value);
        } else {
            throw new CIAMInvalidRequestException("Unsupported name attribute path: " + path);
        }
    }

    private void handleGenericOperation(String op, String path, Object value) {
        switch (path) {
            case "userName":
            case "displayName":
            case "timezone":
                applyAttributeOperation(op, "/preferredTimezone", value); 
                break;
            default:
                throw new CIAMInvalidRequestException("Unsupported attribute path: " + path);
        }
    }

    private void handleEmailOperation(String op, Object value) {
        if (value instanceof List) {
            List<Email> emails = mapper.convertValue(value, new TypeReference<List<Email>>() {});
            String emailAddressesAsString = emails.stream()
                .map(Email::getValue)
                .collect(Collectors.joining(","));
            applyAttributeOperation(op, "/mail", emailAddressesAsString);
        }
    }

    private void handlePhoneNumberOperation(String op, Object value) {
        if (value instanceof List) {
            List<PhoneNumber> phoneNumbers = mapper.convertValue(value, new TypeReference<List<PhoneNumber>>() {});
            String phoneNumberStrings = phoneNumbers.stream()
                .map(PhoneNumber::getValue)
                .collect(Collectors.joining(","));
            applyAttributeOperation(op, "/telephoneNumber", phoneNumberStrings);
        }
    }

    private void handleAddressOperation(String op, String path, Object value) {
        if ("addresses".equals(path)) {
            handleEntireAddressOperation(op, value);
        } else {
            String[] parts = path.split("\\.");
            if (parts.length == 2) {
                handleAddressSubAttributeOperation(op, parts[1], value);
            } else {
                throw new CIAMInvalidRequestException("Unsupported address attribute path: " + path);
            }
        }
    }

    private void handleEntireAddressOperation(String op, Object value) {
        if (value instanceof Map) {
            Address address = mapper.convertValue(value, Address.class);
            applyAttributeOperation(op, "/postalAddress", address.getLocality());
            applyAttributeOperation(op, "/stateProvince", address.getRegion());
            applyAttributeOperation(op, "/postalCode", address.getPostalCode());
            applyAttributeOperation(op, "/country", address.getCountry());
            applyAttributeOperation(op, "/primary", String.valueOf(address.isPrimary()));
        } else {
            throw new CIAMInvalidRequestException("The value for 'addresses' is not of type Address");
        }
    }

    private void handleAddressSubAttributeOperation(String op, String subAttribute, Object value) {
        switch (subAttribute) {
            case "locality":
                applyAttributeOperation(op, "/postalAddress", value);
                break;
            case "region":
                applyAttributeOperation(op, "/stateProvince", value);
                break;
            case "postalCode":
                applyAttributeOperation(op, "/postalCode", value);
                break;
            case "country":
                applyAttributeOperation(op, "/country", value);
                break;
            default:
                throw new CIAMInvalidRequestException("Unsupported address sub-attribute: " + subAttribute);
        }
    }



    public JsonPatch build() {
        return Json.createPatch(operations.build());
    }
}
