Feature: CHIP Oauth2 Token Endpoint

  @Ignore
  Scenario: GET on Token endpoint should return an error
    Given AccessOne Oauth server is available
    When I access the token endpoint using an http GET
    Then the http status code is 405
      And the response has the field error with a value of "method_not_allowed"

  @Ignore
  Scenario: Client Credentials Grant using Valid client credentials should return token
    Given AccessOne Oauth server is available
      And I have a valid client-id
      And I have a valid client-secret
    When I request a token using the client_credentials grant
    Then the http status code is 200
      And the response has the field access_token
      And the response has the field token_type with a value of "Bearer"
      And the response has the field "expires_in"

  @Ignore
  Scenario: Invalid Client ID should return error
    Given AccessOne Oauth server is available
      And I have an invalid client-id
    When I request a token using the client_credentials grant
    Then the http status code is 400
    And the response has the field error with a value of "invalid_client"

  @Ignore
  Scenario: Invalid Client Secret should not return token
    Given AccessOne Oauth server is available
      And I have a valid client-id
      And I have an invalid client-secret
    When I request a token using the client_credentials grant
    Then the http status code is 400
    And the response has the field error with a value of "invalid_client"
    And the response should not have an access_token