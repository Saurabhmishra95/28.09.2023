Feature: Scim /Groups API Endpoint

  Background:
    Given AccessOne Scim server is available

  @CreateGroup @CIAM-488
  Scenario: Create a Group with Required Information
   # displayName will be unique and non-empty
   # description will be provided (can be empty)
    Given I have a valid AccessOne user token
      And I need to create a group
      And the group's displayName will be "TestGroup"
      And the group's description will be "This is a test group"
    When I post to the /scim/v2/Groups Resource
    Then the http status code is 201
      And the response has the field displayName with a value of "TestGroup"
      And the response has the field description with a value of "This is a test group"
      And the response has the field id

  @CreateGroup @CIAM-488
  Scenario: Attempt to Create a Group with the same displayName will result in an 409 error
    Given I have a valid AccessOne user token
      And A group exists with the name TestDuplicateGroup and a description of "A group for testing duplicates"
    When I try to create a group with the same displayName
    Then the http status code is 409
      And the response has the field status with a value of 409
      And the response has the field detail
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @CreateGroup @CIAM-488
  Scenario: Attempt to Create a Group without an access token should return authorization error
    Given I need to create a group
    And the group's displayName will be "ShouldNotCreateGroup"
    When I post to the /scim/v2/Groups Resource
    Then the http status code is 401
    And the response has the field status with a value of 401
    And the response has the field detail
    And the response has the array schemas
    And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @CreateGroup @CIAM-488
  Scenario: Attempt to Create a Group with missing required data will 400 error
    Given I have a valid AccessOne user token
    And I need to create a group
      And the group's description will be "This is a test group"
      # not setting the required displayName
    When I post to the /scim/v2/Groups Resource
    Then the http status code is 400
      And the response has the field status with a value of 400
      And the response has the field detail
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @DeleteGroup @CIAM-488
  Scenario: DELETE Group by id
    Given I have a valid AccessOne user token
      And A group exists with the name TestDeleteGroup and a description of "A group for testing delete group by id"
    When I delete the created Group
    Then the http status code is 204
    And the response body is empty

  @DeleteGroup @CIAM-488
  Scenario: A Deleted Group can no longer be accessed
    Given I have a valid AccessOne user token
      And A group exists with the name TestDeletedGroupIsGone and a description of "A group for testing delete group by id"
      And I delete the created Group
    When I access the group with the its unique id
    Then the http status code is 404

  @GetGroup @CIAM-176 @CIAM-226
  Scenario: GET All Groups
    Given I have a valid AccessOne user token
    When I access the /scim/v2/Groups Resource
    Then the http status code is 200
      And the response has the field totalResults
      And the response has the field startIndex with a value of 1
      And the response has the field itemsPerPage
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:ListResponse"
      And the response has the array resources
      And the response has the field resources[0].schemas[0] with a value of "urn:ietf:params:scim:schemas:core:2.0:Group"
      And the response has the field resources.id

  @GetGroup @CIAM-176 @CIAM-231
  Scenario: GET All Groups without a token should respond with an authorization error
    When I access the /scim/v2/Groups Resource
    Then the http status code is 401
      And the response has the field detail with a value of "Missing or invalid authorization token"
      And the response has the field status with a value of 401
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:Error"

  @GetGroup @CIAM-345
  Scenario: GET Group with basic attributes by id
    Given I have a valid AccessOne user token
      And A group exists with the name TestGetGroupById and a description of "A group for testing get group by id"
    When I access the group with the its unique id
    Then the http status code is 200
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:schemas:core:2.0:Group"
      And the response has the field id
      And the response has the field displayName with a value of "TestGetGroupById"
      And the response has the field description with a value of "A group for testing get group by id"

  @Ignore #TODO Fix when Group members can be created as part of the test setup
  @CIAM-345
  Scenario: GET a specific Group with all the members in the group
    Given I have a valid AccessOne user token
    When I access the /scim/v2/Groups Resource with the unique id 0c459ff5-cc8d-46b4-a30e-e8f56873b5b4
    Then the http status code is 200
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:schemas:core:2.0:Group"
      And the response has the field id with a value of "0c459ff5-cc8d-46b4-a30e-e8f56873b5b4"
      And the response has the field displayName with a value of "ciam-test-role"
      And the response has the field description with a value of "A Role for Testing Assignments"
      And the response has the array members
      And the members array has an entry with the field value with a value of "ab907cce-c0f2-4379-93ed-e6b0c1b4ac3a"
      And the members array has an entry with the field $ref with a value of "http://localhost:8089/scim/v2/Users/ab907cce-c0f2-4379-93ed-e6b0c1b4ac3a"
      And the members array has an entry with the field value with a value of "7923e841-487e-449e-beb3-d2dfdd5c9926"
      And the members array has an entry with the field $ref with a value of "http://localhost:8089/scim/v2/Users/7923e841-487e-449e-beb3-d2dfdd5c9926"

  @PutGroup @CIAM-351
  Scenario: Update Group with ID
    Given I have a valid AccessOne user token
      And A group exists with the name TestPutGroupById and a description of "A group for testing put changes"
      And I need to update the group
      And the group's displayName will be "Updated Group Name"
      And the group's description will be "Updated Group Description"
    When I put the changes to the /scim/v2/Groups Resource
    Then the http status code is 200
    And the response has the field displayName with a value of "Updated Group Name"
    And the response has the field description with a value of "Updated Group Description"

  @PutGroup @CIAM-351
  Scenario: Update Group with missing required displayName
    Given I have a valid AccessOne user token
    And A group exists with the name TestPutGroupById and a description of "A group for testing put changes"
    And I need to update the group
    And the group's description will be "Updated Group Description"
    When I put the changes to the /scim/v2/Groups Resource
    Then the http status code is 400
