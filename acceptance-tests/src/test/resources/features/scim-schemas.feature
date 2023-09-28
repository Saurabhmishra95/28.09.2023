Feature: Scim /scim/v2/Schemas API Endpoint

  Background:
    Given AccessOne Scim server is available

  @CIAM-177 @CIAM-227
  Scenario: GET All Schemas should work without a token
    When I access the /scim/v2/Schemas Resource
    Then the http status code is 200
      And the response has the field totalResults with a value of 2
      And the response has the field startIndex with a value of 1
      And the response has the field itemsPerPage with a value of 2
      And the response has the array schemas
      And the array schemas has the item "urn:ietf:params:scim:api:messages:2.0:ListResponse"
      And the response has the array resources
      And the resources array has an entry with the field id with a value of "urn:ietf:params:scim:schemas:core:2.0:User"
      And the resources array has an entry with the field id with a value of "urn:ietf:params:scim:schemas:core:2.0:Groups"


  @CIAM-230
  Scenario: GET the User Schema should return expected JSON schema response and a valid status code
    When I access the /scim/v2/Schemas Resource with the unique id urn:ietf:params:scim:schemas:core:2.0:User
    Then the http status code is 200
      And the response has the field id with a value of "urn:ietf:params:scim:schemas:core:2.0:User"
      And the response has the field name with a value of "User"
      And the response has the field description with a value of "User Account"
      And the response has the field attributes
      And the attributes array has an entry with the field name with a value of "id"
      And the attributes array has an entry with the field name with a value of "name"
      And the attributes array has an entry with the field name with a value of "displayName"
      And the attributes array has an entry with the field name with a value of "address"
      And the attributes array has an entry with the field name with a value of "emails"
      And the attributes array has an entry with the field name with a value of "phoneNumbers"
      And the attributes array has an entry with the field name with a value of "active"
      And the attributes array has an entry with the field name with a value of "locale"
      And the attributes array has an entry with the field name with a value of "timezone"


  @CIAM-230
  Scenario: GET the Group Schema should return expected JSON schema response and a valid status code
    When I access the /scim/v2/Schemas Resource with the unique id urn:ietf:params:scim:schemas:core:2.0:Groups
    Then the http status code is 200
      And the response has the field id with a value of "urn:ietf:params:scim:schemas:core:2.0:Groups"
      And the response has the field name with a value of "Group"
      And the response has the field description with a value of "Group Resource"
      And the response has the array attributes
      And the attributes array has an entry with the field name with a value of "id"
      And the attributes array has an entry with the field name with a value of "displayName"
      And the attributes array has an entry with the field name with a value of "description"


  @CIAM-230
  Scenario: Requesting a non-existent resource from the SCIM server
    When I access the /scim/v2/Schemas Resource with the unique id urn:ietf:params:scim:schemas:core:2.0:Group
    Then the http status code is 404
      And the response has the array "schemas"
      And the array "schemas" has the item "urn:ietf:params:scim:api:messages:2.0:Error"
      And the response has the field "detail" with a value of "The requested resource could not be found: /scim/v2/Schemas/urn:ietf:params:scim:schemas:core:2.0:Group"
      And the response has the field "status" with a value of 404