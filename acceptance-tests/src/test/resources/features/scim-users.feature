Feature: Scim /scim/v2/Users API Endpoint

  Background:
    Given AccessOne Scim server is available

  @CIAM-194 @CreateUser
  Scenario: Create a User with all required basic information
  #          userName will be all lowercase and contain an domain qualifier
  #          email address value is all lowercase
  #  displayName is created if not supplied

    Given I have a valid AccessOne user token
    And I need to create a user
    And the user's userName will be "TestUser"
    And the user's work email address will be "TestEmail@example.com"
    And the user's name givenName will be "Test"
    And the user's name familyName will be "User"
    When I post to the /scim/v2/Users Resource
    Then the http status code is 201
    And the response has the field userName with a value of "testuser@example.com"
    And the response has the field name.givenName with a value of "Test"
    And the response has the field name.familyName with a value of "User"
    And the response has the field name.formatted with a value of "Test User"
    And the response has the field displayName with a value of "Test User"
    And the response has the field id
    And the response has the array emails
    And the emails array has an entry with the field type with a value of "work"
    And the emails array has an entry with the field value with a value of "testemail@example.com"

  @CIAM-194
  Scenario: Attempt to Create a User with the same userName will result in an 409 error
    Given I have a valid AccessOne user token
    And A user exists with a userName of DuplicateUser
    When I try to create a user with the same userName
    Then the http status code is 409
    And the response has the field status with a value of 409
    And the response has the field detail
    And the response has the array schemas
    And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @CIAM-194
  Scenario: Attempt to Create a User without an access token should return authorization error
    Given I need to create a user
    And the user's userName will be "NoTokenUser"
    And the user's work email address will be "notoken@example.com"
    And the user's name givenName will be "Test"
    And the user's name familyName will be "User"
    When I post to the /scim/v2/Users Resource
    Then the http status code is 401

  @DeleteUser @CIAM-194
  Scenario: DELETE User by id
    Given I have a valid AccessOne user token
    And A user exists with a userName of DeleteMe
    When I delete the created User
    Then the http status code is 204
    And the response body is empty

  @DeleteUser @CIAM-194
  Scenario: A Deleted User can no longer be accessed
    Given I have a valid AccessOne user token
    And A user exists with a userName of IShouldBeGone
    And I delete the created User
    When I access the user with the its unique id
    Then the http status code is 404

  @CIAM-175 @CIAM-225
  Scenario: GET All Users should return a list of Users with the expected JSON response and status code
    Given I have a valid AccessOne user token
    When I access the /scim/v2/Users Resource
    Then the http status code is 200
      And the response has the field totalResults
      And the response has the field startIndex with a value of 1
      And the response has the field itemsPerPage
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:ListResponse"
      And the response has the array resources
      And the response has the field resources[0].schemas[0] with a value of "urn:ietf:params:scim:schemas:core:2.0:User"
      And the response has the field resources.id
      And the resources array has an entry with the field id with a value of "4c4e2537-2621-4911-a723-1c2b52b57b01"
      And the resources array has an entry with the field userName with a value that's not null

  @CIAM-175 @CIAM-231
  Scenario: GET All Users without a token should respond with an authorization error
    When I access the /scim/v2/Users Resource
    Then the http status code is 401
    And the response has the field detail with a value of "Missing or invalid authorization token"
    And the response has the field status with a value of 401
    And the response has the array schemas
    And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @CIAM-228 @CIAM-442 @CreateUser
  Scenario: GET Single User using valid id should return all attributes that user with the expected JSON response and status code
    Given I have a valid AccessOne user token
    And A user exists with all fields for GetTest User
    When I access the user with the its unique id
    Then the http status code is 200
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:schemas:core:2.0:User"
      And the response has the field id
      And the response has the field userName with a value of "gettest.user@example.com"
      And the response has the field name.givenName with a value of "GetTest"
      And the response has the field name.familyName with a value of "User"
      And the response has the field name.formatted with a value of "GetTest User"
      And the response has the field displayName with a value of "GetTest User"
      And the response has the array emails
      And the emails array has an entry with the field type with a value of "work"
      And the emails array has an entry with the field primary with a value of true
      And the emails array has an entry with the field value with a value of "gettest.user@example.com"
      And the response has the field meta.version
      And the response has the field meta.location

  @CIAM-228
  Scenario: GET Single User using an invalid userId should return an error response
    Given I have a valid AccessOne user token
    When I access the /scim/v2/Users Resource with the unique id not-a-real-userid
    Then the http status code is 404
    And the response has the field status with a value of 404
    And the response has the field detail
    And the response has the array schemas
    And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @CIAM-228
  Scenario: GET Single User without an authorization token should return an error response
    When I access the /scim/v2/Users Resource with the unique id no-a-real-userid
    Then the http status code is 401
    And the response has the field status with a value of 401
    And the response has the field detail with a value of "Missing or invalid authorization token"
    And the response has the array schemas
    And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

    @Ignore
    @CIAM-346
    Scenario: GET Single User with group membership should return user details, including group references
      Given I have a valid AccessOne user token
      When I access the /scim/v2/Users Resource with the unique id 7923e841-487e-449e-beb3-d2dfdd5c9926
      Then the http status code is 200
      And the response has the array groups
      And the groups array has an entry with the field value with a value of "0c459ff5-cc8d-46b4-a30e-e8f56873b5b4"
      And the groups array has an entry with the field $ref with a value of "http://localhost:8089/Groups/0c459ff5-cc8d-46b4-a30e-e8f56873b5b4"

  @CIAM-518
  Scenario: Update User with ID
    Given I have a valid AccessOne user token
    And A user exists with all fields for PutTest User
    And I need to update the user
    And the user's work email address will be "modifiedemail@example.com"
    And the user's name givenName will be "ModifiedGivenName"
    And the user's name familyName will be "ModifiedFamilyName"
    And the user's userName will be "modifiedemail@example.com"
    When I put the changes to the /scim/v2/Users Resource
    Then the http status code is 200
    And the response has the field userName with a value of "modifiedemail@example.com"
    And the response has the field name.givenName with a value of "ModifiedGivenName"
    And the response has the field name.familyName with a value of "ModifiedFamilyName"
    And the response has the array emails
    And the emails array has an entry with the field type with a value of "work"
    And the emails array has an entry with the field primary with a value of true
    And the emails array has an entry with the field value with a value of "modifiedemail@example.com"
    And the response has the field meta.version
    And the response has the field meta.location

  @CIAM-518
  Scenario: Update User with missing required attributes
    Given I have a valid AccessOne user token
    And A user exists with all fields for PutTest User
    And I need to update the user
    And the user's work email address will be "modifiedemail@example.com"
    When I put the changes to the /scim/v2/Users Resource
    Then the http status code is 400

