
Feature: Portal /portal API Endpoint

  Background:
    Given AccessOne Scim server is available


  @CIAM-576
  Scenario: Retrieve application details using /myapplications endpoint with a valid token
    Given I have a valid AccessOne user token
    And I access the portal/applicationdetails Resource
    Then the http status code is 200
    And the response has the field section with a value of "myApps"
    And the response has an array apps
    And the apps array has an entry with the field appId with a value of "c49c48c7-271b-48cc-a8a1-f470d0b26766"
    And the apps array has an entry with the field appName with a value of "eCare NEXT®"
    And the apps array has an entry with the field appDescription with a value of "eCare NEXT® is a central rules engine that provides intelligent automation to drive exception-based workflows."
    And the apps array has an entry with the field appIcon with a value of "blue"
    And the apps array has an entry with the field appUrl with a value of "https://www.ecarenext.com/eCareNext"
    And the apps array has an entry with the field appId with a value of "8b8e619a-a0d0-4335-a27e-6bada1f72e8a"
    And the apps array has an entry with the field appName with a value of "OneSource"
    And the apps array has an entry with the field appDescription with a value of "OneSource is a standalone web-based portal that provides access to a range of patient access services."
    And the apps array has an entry with the field appIcon with a value of "green"
    And the apps array has an entry with the field appUrl with a value of "https://onesource.passporthealth.com/"







