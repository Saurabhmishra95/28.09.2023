
Feature: Scim /Me API Endpoint

  Background:
    Given AccessOne Scim server is available


  @CIAM-538
  Scenario: Retrieve user details using /Me endpoint with a valid token
    Given I have a valid AccessOne user token
    And I access the scim/v2/Me Resource
    Then the http status code is 200
    And the response has the field userName with a value of "michael.petras@experian.com"
    And the response has the field name.givenName with a value of "Mike"
    And the response has the field name.familyName with a value of "Petras"
    And the response has the array emails
    And the emails array has an entry with the field type with a value of "work"
    And the emails array has an entry with the field primary with a value of true
    And the emails array has an entry with the field value with a value of "michael.petras@experian.com"
    And the response has the field meta.version
    And the response has the field meta.location




