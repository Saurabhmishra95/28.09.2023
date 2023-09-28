package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.EnvironmentSettings;
import com.experianhealth.ciam.exception.CIAMForbiddenException;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.exception.CIAMPasswordException;
import com.experianhealth.ciam.exception.CIAMRuntimeException;
import com.experianhealth.ciam.exception.CIAMUnauthorizedException;
import com.experianhealth.ciam.forgerock.model.User;
import com.experianhealth.ciam.scimapi.utils.HttpUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ForgeRockAMServiceImpl implements ForgeRockAMService {
    public ForgeRockAMServiceImpl() {
        this.amBaseUrl = EnvironmentSettings.getAmBaseUrl();
        this.httpClient = HttpUtils.createAcceptSelfSignedCertificateClient();
    }
    private final String amBaseUrl;
    public CloseableHttpClient httpClient;


    @Override
    public User getUserInfo(String token) {
        String userInfoUrl = amBaseUrl + "/am/oauth2/userinfo";
        HttpGet httpGet = new HttpGet(userInfoUrl);
        httpGet.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String jsonResponse = EntityUtils.toString(entity);
                return parseUserInfo(jsonResponse);
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new CIAMUnauthorizedException("Token is invalid or expired.");
            } else {
                throw new RuntimeException("Failed to retrieve user info from AM. Status code: " + statusCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while communicating with AM", e);
        }
    }

    @Override
    public String getAccessToken(String clientID, String secret, String userName, String password) {
        String tokenUrl = amBaseUrl + "/am/oauth2/access_token";

        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", "password"));
        params.add(new BasicNameValuePair("client_id", "scim-client"));
        params.add(new BasicNameValuePair("client_secret", "1Password"));
        params.add(new BasicNameValuePair("username", userName));
        params.add(new BasicNameValuePair("password", password));

        HttpPost httpPost = new HttpPost(tokenUrl);
        httpPost.setHeader(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded");

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, StandardCharsets.UTF_8));
            String jsonResponse = sendPostRequest(httpPost);

            CloseableHttpResponse response = httpClient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            response.close();

            if (statusCode == HttpStatus.SC_OK) {
                return extractAccessToken(jsonResponse);
            } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
                throw mapBadRequestError(httpPost, response);
            } else {
                CIAMRuntimeException exception = mapErrorResponse(httpPost, response);
                throw exception;
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while getting access token", e);
        }
    }

    private String sendPostRequest(HttpUriRequest request) {
        try (CloseableHttpResponse response = httpClient.execute(request)) {
            mapErrorResponse(request, response);
            HttpEntity entity = response.getEntity();
            return EntityUtils.toString(entity);
        } catch (IOException e) {
            throw new RuntimeException("Error while sending POST request", e);
        }
    }

    private CIAMRuntimeException mapErrorResponse(HttpUriRequest request, CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        CIAMRuntimeException exception;

        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            exception = new CIAMUnauthorizedException("Unauthorized request: " + request);
        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            exception = new CIAMForbiddenException("Forbidden Request" + request);
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            exception = new CIAMNotFoundException(request.getURI().getPath(), "Not Found Request: " + request);
        } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
            exception = mapBadRequestError(request, response);
        } else {
            exception = new CIAMRuntimeException("Unexpected error with request: " + request + ", Response: " + response.getStatusLine());
        }

        return exception;
    }

    private CIAMRuntimeException mapBadRequestError(HttpUriRequest request, CloseableHttpResponse response) {
        return new CIAMPasswordException("Incorrect Current Password");
    }

    private User parseUserInfo(String jsonResponse) {
        try {
            if (jsonResponse == null) {
                throw new IllegalArgumentException("JSON response is null");
            }

            // Create a JSON object from the response
            JsonObject jsonObject = Json.createReader(new StringReader(jsonResponse)).readObject();

            // Print the JSON object for debugging
            System.out.println("Parsed JSON Object: " + jsonObject.toString());

            // Extract relevant user information from the JSON object
            String username = jsonObject.getString("username");
            String id=jsonObject.getString("sub");
            User user=new User();
            user.setUserName(username);
            user.set_id(id);
            System.out.println(user);
            return user;
        } catch (JsonParsingException e) {
            System.err.println("Error parsing user info JSON: " + e.getMessage());
            throw new RuntimeException("Error parsing user info JSON", e);
        } catch (IllegalArgumentException e) {
            System.err.println("Error parsing user info: " + e.getMessage());
            throw new RuntimeException("Error parsing user info", e);
        }
    }

    private String extractAccessToken(String jsonResponse) {
        try {
            try (JsonReader jsonReader = Json.createReader(new StringReader(jsonResponse))) {
                JsonObject jsonObject = jsonReader.readObject();
                return jsonObject.getString("access_token");
            }
        } catch (JsonException e) {
            throw new RuntimeException("Error extracting access token from JSON", e);
        }
    }



}
