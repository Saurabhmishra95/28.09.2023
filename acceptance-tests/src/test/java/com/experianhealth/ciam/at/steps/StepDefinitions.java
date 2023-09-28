package com.experianhealth.ciam.at.steps;

import com.experianhealth.ciam.at.model.*;
import com.google.common.collect.Lists;
import io.cucumber.java.After;
import io.cucumber.java.ParameterType;
import io.cucumber.java.en.*;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.List;
import java.util.Stack;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Implementation of the Scenario Steps used within the Feature files.
 * This class is stateful as each step builds a given RequestSpecification
 * or checks values within the Response.
 *
 * Each Step implementation should only perform the minimal amount  to accomplish the required check or action.
 * This allow using the Steps with in mulitple feature files and combining them in multiple ways to perform a given test.
 */
public class StepDefinitions {
    public static final String USERS = "/scim/v2/Users";
    public static final String GROUPS = "/scim/v2/Groups";
    private String userToken = null;
    private RequestSpecification scimRequest;
    private RequestSpecification tokenRequest;
    private Response response;
    private JSONObject scimResource;
    private String randomPrefix = null;
    private OauthServer tokenServer;
    private ScimServer scimServer;
    private boolean logAll = false;
    private Stack<String> createdLocations = new Stack<String>();
    private String deletedResource;


    public StepDefinitions() {
        String logAllStr = System.getenv("AT_LOGALL");
        if(StringUtils.isNotEmpty(logAllStr)){
            logAll = Boolean.valueOf(logAllStr);
        }
    }

    private String getFieldValue(String field) {
        JsonPath jsonPathEvaluator = response.jsonPath();
        return jsonPathEvaluator.get(field);
    }
    private  RequestSpecification configureLogging(RequestSpecification rs){
        if(logAll){
            rs.log().all();
        }
        return rs;
    }
    private  Response configureLogging(Response response){
        if(logAll){
            response.then().log().all();
        }
        return response;
    }
    RequestSpecification getRequestSpecification(APIServer apiServer) {
        RequestSpecification rs = io.restassured.RestAssured.given()
                .baseUri(apiServer.getBaseUri())
                .basePath(apiServer.getBasePath())
                .port(apiServer.getPort())
                .relaxedHTTPSValidation();
        configureLogging(rs);
        return rs;
    }

    /**
     * create a custom parameter type for a Boolean
     * @param value
     * @return
     */
    @ParameterType(value = "true|false")
    public Boolean booleanValue(String value){
        return Boolean.valueOf(value);
    }

    @Given("I have a valid {word} user token")
    public StepDefinitions validUserToken(String tokenProviderName) {
        userToken = this.oauthServerIsAvailable(tokenProviderName)
                .iHaveAValidOauthClientId()
                .iHaveAValidOauthClientSecret()
                .iHaveAValidAdminUsername()
                .iHaveAValidAdminPassword()
                .iRequestATokenWithGrantType("password")
                .theResponseContainsKeyWithAnyValue("access_token")
                .getFieldValue("access_token");
        withUserBearerToken(userToken);
        return this;
    }

    @Given("{word} Scim server is available")
    public StepDefinitions isScimServerAvailable(String serverName) {
        scimServer = APIServerFactory.createScimServer(serverName);
        scimRequest = getRequestSpecification(scimServer);
        Response r = configureLogging(scimRequest.get(scimServer.getHealthCheckPath()));

        r.then().assertThat().statusCode(not(nullValue()));
        return this;
    }

    @When("I access the {word} Resource")
    public void iAccessTheResource(String resourceName) {
        response = configureLogging(scimRequest.get(resourceName));
    }
    @When("I access the {word} Resource with the unique id {word}")
    public void iAccessTheUsersResourceWithTheUniqueIdWord(String resourceName, String id) {
        iAccessTheResource(resourceName + "/" + id);
    }

    @When("I access the user with the its unique id")
    @When("I access the group with the its unique id")
    public void iAccessTheResource() {
        String resourceLocation = null;
        if(createdLocations.size() > 0){
            resourceLocation = createdLocations.firstElement();
        }else if (deletedResource != null){
            resourceLocation = deletedResource;
        }else {
            fail("No Resourcce is available that was created or deleted");
        }
        iAccessTheResource(resourceLocation);

    }

    @When("I delete the {word} Resource with the unique id {word}")
    public void iDeleteTheResourceWithTheUniqueId(String resourceName, String id) {
        response = configureLogging(scimRequest.delete(resourceName + "/" + id));
    }

    @When("I delete the created Resource")
    @When("I delete the created Group")
    @When("I delete the created User")
    public void iDeleteTheCreatedResource() {
        assertTrue(createdLocations.size() > 0);
        deletedResource = createdLocations.pop();
        deleteResource(deletedResource);
    }

    @After //Delete any created resource after the scenario completes
    public void cleanupResources() {
        while( createdLocations.size() > 0) {
            deleteResource(createdLocations.pop());
        }
        randomPrefix = null;
    }
    @And("the response body is empty")
    public void theResponseBodyIsEmpty() {
        assertEquals("",response.getBody().asString());
    }

    public void deleteResource(String resource) {
        if(StringUtils.isNotEmpty(resource)){
            scimRequest = getRequestSpecification(scimServer);
            withUserBearerToken(userToken);
            response = configureLogging(scimRequest.delete(resource));
        }
    }
    public void withUserBearerToken(String userToken) {
        scimRequest.with().header("Authorization", "Bearer " + userToken);
    }
    @Given("{word} Oauth server is available")
    public StepDefinitions oauthServerIsAvailable(String tokenProviderName) {
        tokenServer = APIServerFactory.createOauthServer(tokenProviderName);
        tokenRequest = getRequestSpecification(tokenServer);

        Response r = configureLogging(tokenRequest.get(tokenServer.getHealthCheckPath()));
        r.then().assertThat().statusCode(200);
        return this;
    }

    @Given("I have a valid client-id")
    public StepDefinitions iHaveAValidOauthClientId() {
        setClientId(tokenServer.getValidClientId());
        return this;
    }
    @Given("I have a valid client-secret")
    public StepDefinitions iHaveAValidOauthClientSecret() {
        setClientSecret(tokenServer.getValidClientSecret());
        return this;
    }

    @Given("I have a valid admin username")
    public StepDefinitions iHaveAValidAdminUsername() {
        setUsername(tokenServer.getValidAdminUsername());
        return this;
    }

    @Given("I have a valid admin password")
    public StepDefinitions iHaveAValidAdminPassword() {
        setPassword(tokenServer.getValidAdminPassword());
        return this;
    }
    @Given("I have an invalid client-id")
    public StepDefinitions iHaveAnInvalidOauthClientId() {
        setClientId("no-a-real-client-id");
        return this;
    }
    @Given("I have an invalid client-secret")
    public StepDefinitions iHaveAnInvalidOauthClientSecret() {
        setClientSecret("not-a-valid-secret");
        return this;
    }

    @When("I request a token using the {word} grant")
    public StepDefinitions iRequestATokenWithGrantType(String grantType){
        tokenRequest.with()
                .formParam("grant_type", grantType)
                .formParam("scope", "openid profile fr:idm:*");
        response = configureLogging(tokenRequest.post(tokenServer.getTokenPath()));
        return this;
    }

    @When("I access the token endpoint using an http GET")
    public void iAccessTheTokenEndpointUsingAGet() {
        response = configureLogging(tokenRequest.get(tokenServer.getTokenPath()));
    }

    public void setClientId(String clientId) {
        tokenRequest.with().formParam("client_id", clientId);
    }
    public void setClientSecret(String secret) {
        tokenRequest.with().formParam("client_secret", secret);
    }

    public void setUsername(String username) {
        tokenRequest.with().formParam("username", username);
    }
    public void setPassword(String password) {
        tokenRequest.with().formParam("password", password);
    }

    @Then("the http status code is {int}")
    public StepDefinitions theStatusCodeIs(int statusCode) {
        response.then().assertThat().statusCode(statusCode);
        return this;
    }

    @Then("the response has the field {word} with a value of {string}")
    public StepDefinitions theResponseContainsKeyValue(String key, String value){
        String actualValue = getFieldValue(key);
        //TODO Add a debug LOGGER
        //System.out.println("RandomPrefix: " + randomPrefix);
        //System.out.println("Key: '" + key +"'");
        //System.out.println("Value: " + value);
        //System.out.println("ActualValue: " + actualValue);
        if(randomPrefix != null) {
            if(StringUtils.equalsIgnoreCase("userName", key) ){
                value = randomPrefix + "." + value ;
            }
            else if(StringUtils.equalsIgnoreCase("displayName", key)){
                /* displayName may or may not have the hidden random prefix */
                if (StringUtils.startsWithIgnoreCase(getFieldValue(key), randomPrefix) ) {
                    value = randomPrefix + "." + value ;
                }
            }
        }
        //System.out.println("CompareValue: " + actualValue);
        response.then().assertThat().body(key, is(value));
        return this;
    }

    @Then("the response has the field {word} with a value of {int}")
    public StepDefinitions theResponseContainsKeyValue(String key, int value){
        response.then().assertThat().body(key, is(value));
        return this;
    }

    @Then("the response has the field {word}")
    public StepDefinitions theResponseContainsKeyWithAnyValue(String key){
        response.then().assertThat().body(key, not(nullValue()));
        return this;
    }

    @Then("the response should not have an {word}")
    public StepDefinitions theResponseDoesNotContainKey(String key){
        response.then().assertThat().body(key, nullValue());
        return this;
    }

    @Then("the response has the array {word}")
    public StepDefinitions theResponseHasTheArray(String key){
        response.then().assertThat().body(key, isA(List.class));
        return this;
    }

    @And("the array {word} has the item {string}")
    @And("the response array {word} has the item {string}")
    public void theArrayHasTheItem(String arrayKey, String value) {
        response.then().assertThat().body(arrayKey, hasItems(value));
    }

    @And("the array {word} has the item {word} with a value of {string}")
    @And("the {word} array has an item {word} with a value of {string}")
    @And("the {word} array has an entry with the field {word} with a value of {string}")
    public void theArrayHasTheItemWithAValueOf(String arrayKey, String field, String value) {
        response.then().assertThat().body(arrayKey + "." + field, hasItems(value));
    }

    @And("the {word} array has an entry with the field {word} with a value of {booleanValue}")
    public void theArrayHasTheItemWithAValueOf(String arrayKey, String field, Boolean value) {
        response.then().assertThat().body(arrayKey + "." + field, hasItems(value));
    }

    @And("the {word} array has an entry with the field {word} with a value that's not null")
    public void theArrayHasTheItemWithAnyValue(String arrayKey, String field) {
        response.then().assertThat().body(arrayKey + "." + field, hasItems(not(nullValue())));
    }

    @And("I need to create a user")
    @And("I need to update the user")
    @And("I need to create a group")
    @And("I need to update the group")
    public void iNeedToCreateAResource() {
        scimResource = new JSONObject();
    }

    @And("the {word}'s {word} will be {string}")
    public void theResourceWillHaveFieldWithValue(String resourceType, String fieldName, String fieldValue) {
        if(StringUtils.equalsIgnoreCase("user", resourceType) && StringUtils.equalsIgnoreCase("userName",fieldName) ) {
            //userName must be unique. Add a random prefix to the userName to avoid potential conflicts.
            fieldValue = generateRandomPrefix() + "." + fieldValue;
        }else if(StringUtils.equalsIgnoreCase("group", resourceType) && StringUtils.equalsIgnoreCase("displayName",fieldName) ) {
            fieldValue = generateRandomPrefix() + "." + fieldValue;
        }
        scimResource.put(fieldName, fieldValue);
    }
    @And("the user's name {word} will be {string}")
    public void theUsersWillHaveANameFieldWithValue(String fieldName, String fieldValue) {
        JSONObject name;
        if(scimResource.has("name")){
            name = scimResource.getJSONObject("name");
        }else {
            name = new JSONObject();
            scimResource.put("name", name);
        }
        name.put(fieldName, fieldValue);
    }

    @And("the user's {word} email address will be {string}")
    public void theUserEmailAddressWillBe(String emailType, String emailValue) {
        JSONArray emails;
        if (scimResource.has("emails")) {
            emails = scimResource.getJSONArray("emails");
        }else {
            emails = new JSONArray();
            scimResource.put("emails", emails);
        }
        JSONObject email = new JSONObject();
        email.put("type", emailType);
        email.put("value", emailValue);
        emails.put(email);
    }

    @When("I post to the {word} Resource")
    public void iPostToTheResource(String resource) {

        scimRequest.contentType(ContentType.JSON).body(scimResource.toString());
        response = configureLogging(
                scimRequest.post(resource)
        );
        if ( response.statusCode() == 201 ) {
            createdLocations.push(response.getHeader("Location"));
        }
    }

    @When("I put the changes to the {word} Resource")
    public void iPutTheChangesToTheResource(String resource) {

        String resourceLocation = resource;
        System.out.println("resource: " + resource);
        System.out.println("createdLocations: " + createdLocations);
        if(createdLocations.size() > 0) {
            //look for a newly created resource to update
            //if it matches the resource type, use that as the location
            if (StringUtils.startsWithIgnoreCase(createdLocations.firstElement(), resource)) {
                resourceLocation = createdLocations.firstElement();
                String[] parts = StringUtils.split(resourceLocation, '/');
                String id = parts[parts.length-1];
                scimResource.put("id", id);
            }
        }
        scimRequest.contentType(ContentType.JSON).body(scimResource.toString());
        response = configureLogging(
                scimRequest.put(resourceLocation)
        );
    }

    private String generateRandomPrefix() {
        if(randomPrefix==null){
            randomPrefix = StringUtils.lowerCase(RandomStringUtils.randomAlphanumeric(6));
        }
        return randomPrefix;
    }

    @Given("A user exists with a userName of {word}")
    /**
     * execute the minimum steps to create a user with the given userName
     */
    public void aUserExistsWithAUserNameOfWord(String fieldValue) {
        iNeedToCreateAResource();
        theResourceWillHaveFieldWithValue("user", "userName", fieldValue);
        theUserEmailAddressWillBe("work", fieldValue + "@example.com");
        theUsersWillHaveANameFieldWithValue("givenName", "CreatedGivenName");
        theUsersWillHaveANameFieldWithValue("familyName", "CreatedFamilyName");

        iPostToTheResource(USERS);
        theStatusCodeIs(201);
    }

    @Given("A user exists with all fields for {word} {word}")
    /**
     * execute the minimum steps to create a user with the given userName
     */
    public void aUserExistsWithAllFields(String givenName, String familyName) {
        iNeedToCreateAResource();
        String userName = givenName + "." + familyName;
        theResourceWillHaveFieldWithValue("user", "userName",userName);
        theUserEmailAddressWillBe("work", userName+ "@example.com");
        theResourceWillHaveFieldWithValue("user", "displayName", givenName + " " + familyName);
        theUsersWillHaveANameFieldWithValue("givenName", givenName);
        theUsersWillHaveANameFieldWithValue("familyName", familyName);

        iPostToTheResource(USERS);
        theStatusCodeIs(201);

    }

    @Given("A group exists with the name {word} and a description of {string}")
    public void aGroupExistsWith(String groupName, String groupDescription) {
        iNeedToCreateAResource();
        theResourceWillHaveFieldWithValue("group", "displayName",groupName);
        theResourceWillHaveFieldWithValue("group", "description",groupDescription);
        iPostToTheResource(GROUPS);
        theStatusCodeIs(201);

    }

    @When("I try to create a user with the same userName")
    public void iTryToCreateAUserWithTheSameUserName() {
        //verify the existing response has a userName that matches the current scimUser.
        theResponseContainsKeyWithAnyValue("userName");
        //post the same request
        iPostToTheResource(USERS);
    }

    @When("I try to create a group with the same displayName")
    public void iTryToCreateAGroupWithTheSameDisplayName() {
        //verify the existing response has a userName that matches the current scimUser.
        theResponseContainsKeyWithAnyValue("displayName");
        //post the same request
        iPostToTheResource(GROUPS);
    }

    @Then("the response has an array apps")
    public void the_response_has_an_array_apps() {
        List<Map<String, Object>> apps = response.jsonPath().getList("apps");

        if (apps == null || apps.isEmpty()) {
            throw new AssertionError("The 'apps' array is missing or empty in the response.");
        }
    }


}
