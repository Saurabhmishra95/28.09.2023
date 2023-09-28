API Acceptance and Regression Tests
===================================

Overview
--------

This module is a framework for testing the Scim and Portal APIs as an integration test. 
This testing system is dependent upon the API server and all of its dependencies being available. 
It uses the Behavioral Driven Design approach for specifying expected behavior for each API call.
The Gherkin language is used to document each scenario using a Given, Then, When approach.

Frameworks
----------
This testing module uses several frameworks: Java Cucumber, Rest Assured, 
which in turn uses Junit, JasonPath, and  Hamcrest. 
The Cucumber framework drives the execution of the test by processing the Gherkin specified in Feature files.
Each line within the Feature file is matched against a Step Definition that is identified by string/regex within 
a @Given, @When, @Then, @And, etc annotation. To learn more about these frameworks, visit these sites:
* https://cucumber.io
* https://rest-assured.io/
* https://github.com/json-path/JsonPath
* https://hamcrest.org/JavaHamcrest/

Directory Structure
-------------------
* src/test/java - The location of the Java source code for implementing the StepDefinitions
* src/test/resources/features - The location of the feature files that define the intent of the API 

### Key Files
* src/test/resources/features/scim-users.feature - sample feature file for the /scim/v2/Users endpoint
* src/java/com/experianhealth/ciam/at/steps/StepDefinition.java - 
  The Java code tha implements the Step Definitions found within the feature files.

Example Scenario
----------------
```Gherkin
Background: Given the AccessOne Scim server is available

@CIAM-231
Scenario: GET All Users without a token should respond with an authorization error
When I access the /Users Resource
Then the http status code is 401
  And the response contains "detail" with value "Missing or invalid authorization token"
  And the response contains "status" with value 401
  And the response has the array "schemas"
  And the array "schemas" has the item "urn:ietf:params:scim:api:messages:2.0:Error"
```

With this simple paragraph, we can see how the SCIM API server should respond when a client access the /Users resource
without an Authorization Bearer token. Notice how each *step* only checks or performs a single action. This style allows
for reusing step definitions across multiple scenarios.

Note: Add @Tags before each scenario, especially scenarios that exercise a given Jira ticket number

Running the Tests
-----------------
The test use Maven surefire test plugin for executing the tests. 
A Makefile exists for easier execution of the tests with various options for controlling which tests to execute
The following environment variables must be set before running the tests.

`AM_BASEURL` [required] The base url to the Oauth server

`SCIM_BASEURL` [required] the base url to the Scim server to test

`AT_LOGALL` [optional] turn on/off request/response logging during the test. Default: `false`

Note: the AT code expects those environment variables to be set. 
The Makefile will set reasonable default values for the BASEURLS if not set in the environment.

The SCIM / Portal Server must be running prior to executing the tests.

### Examples:
* Set your environment variables
```bash
    AM_BASEURL=https://ehfops.idmworks.net
    SCIM_BASEURL=http://localhost:8089
    AT_LOGALL=true
```
* Run all tests not tagged with @Ignore
```bash
    make test
```

* Run all tests with a given tag(s)
```bash
    make test TAGS="@CIAM-175"
```

* Run all tests for a given Feature and ignore any Scenario tagged with @Ignore
```bash
    make test FEATURE="scim-users.feature"
```


