package com.experianhealth.ciam.forgerock.service.helper;

import com.experianhealth.ciam.exception.CIAMRuntimeException;
import com.experianhealth.ciam.forgerock.service.AbstractForgeRockIDMServiceImpl;
import com.experianhealth.ciam.scimapi.utils.HttpUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonPatch;
import javax.json.JsonString;
import javax.json.JsonValue;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Helper class for sending ForgeRock IDM HTTP Requests and returning a Reader for the Response
 */
public class HTTPRequestProcessor{
    protected static final Logger LOGGER = Logger.getLogger(AbstractForgeRockIDMServiceImpl.class.getName());
    public static Reader sendPostRequest(URI uri, String token, Object entity) {
        return sendRequest(createPostRequest(uri, entity), token);
    }

    public static  Reader sendGetRequest(String token, URI uri) {
        return sendRequest(new HttpGet(uri), token);
    }
    public static Reader sendPutRequest(String token, URI uri, Object object) {
        return sendRequest(createPutRequest(uri, object), token);
    }
    public static Reader sendPatchRequest(String token, URI uri, JsonPatch jsonPatch) {
        return sendRequest(createPatchRequest(uri, jsonPatch), token);
    }
    public static Reader sendDeleteRequest(String token, URI uri) {
        return sendRequest(new HttpDelete(uri), token);
    }

    static protected HttpPost createPostRequest(URI uri, Object entity) {
        HttpPost post = new HttpPost(uri);
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(entity);
            LOGGER.info("HttpPost Content: " + json);
            StringEntity postEntity = new StringEntity(json);
            post.setEntity(postEntity);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        post.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        LOGGER.info("HttpPost: " + post);
        LOGGER.info("HttpPost Headers:" + post.getAllHeaders());
        LOGGER.info("HttpPost Body: " + post.getEntity());
        return post;
    }
    static protected HttpPut createPutRequest(URI uri, Object object) {
        HttpPut put = new HttpPut(uri);
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();
        try {
            String json = ow.writeValueAsString(object);
            put.setEntity(new StringEntity(json));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        put.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
        return put;
    }
    static protected HttpPatch createPatchRequest(URI uri, JsonPatch jsonPatch) {
        HttpPatch patch = new HttpPatch(uri);
        List<Map<String, Object>> operations = convertJsonPatchToList(jsonPatch);
        String json = convertOperationsToJson(operations);
        StringEntity entity = createJsonEntity(json);
        patch.setEntity(entity);
        LOGGER.info("PatchRequest: " + patch);
        LOGGER.info("PatchBody: " + json);
        return patch;
    }

    static protected Reader sendRequest(HttpUriRequest request, String token) {

        request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        request.setHeader(HttpHeaders.ACCEPT, "application/json");
        LOGGER.info("Sending Request: " + request);

        try (CloseableHttpClient httpClient = HttpUtils.createAcceptSelfSignedCertificateClient();
             CloseableHttpResponse response = httpClient.execute(request)) {
            LOGGER.info("Response: " + response);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED) {
                HttpEntity entity = response.getEntity();
                ByteArrayOutputStream body = new ByteArrayOutputStream();
                entity.writeTo(body);
                LOGGER.info("ResponseBody: " + body);
                Reader reader = new InputStreamReader(new ByteArrayInputStream(body.toByteArray()));
                return reader;
            }
            CIAMRuntimeException exception = FRErrorExceptionMapper.mapErrorResponse(request, response);
            throw exception;
        } catch (IOException e) {
            throw new CIAMRuntimeException("Failed to send http request" + request, e);
        }
    }
    static protected List<Map<String, Object>> convertJsonPatchToList(JsonPatch patch) {
        List<Map<String, Object>> operations = new ArrayList<>();
        JsonArray jsonArray = patch.toJsonArray();

        for (JsonValue jsonValue : jsonArray) {
            JsonObject jsonObject = jsonValue.asJsonObject();
            Map<String, Object> operation = createOperationMap(jsonObject);
            operations.add(operation);
        }
        return operations;
    }


    static private String convertOperationsToJson(List<Map<String, Object>> operations) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(operations);
        } catch (JsonProcessingException e) {
            throw new CIAMRuntimeException("Failed to create JSON from operations", e);
        }
    }

    static private StringEntity createJsonEntity(String json) {
        try {
            StringEntity entity = new StringEntity(json);
            entity.setContentType("application/json");
            return entity;
        } catch (UnsupportedEncodingException e) {
            throw new CIAMRuntimeException("Failed to create JSON entity", e);
        }
    }

    static private Map<String, Object> createOperationMap(JsonObject jsonObject) {
        Map<String, Object> operation = new HashMap<>();
        
        if (jsonObject.containsKey("op")) {
            operation.put("operation", jsonObject.getString("op"));
        }
        
        if (jsonObject.containsKey("path")) {
            operation.put("field", jsonObject.getString("path"));
        }
        
        if (jsonObject.containsKey("value")) {
            JsonValue value = jsonObject.get("value");
            if (value != null) {
                if (value.getValueType() == JsonValue.ValueType.ARRAY) {
                    JsonArray array = value.asJsonArray();
                    List<String> values = new ArrayList<>();
                    for (JsonValue arrayValue : array) {
                        if (arrayValue.getValueType() == JsonValue.ValueType.OBJECT) {
                            JsonObject obj = arrayValue.asJsonObject();
                            if (obj.containsKey("value")) {
                                values.add(obj.getString("value"));
                            }
                        }
                    }
                    operation.put("value", values);
                } else if (value.getValueType() == JsonValue.ValueType.STRING) {
                    operation.put("value", ((JsonString) value).getString());
                } else if (value.getValueType() == JsonValue.ValueType.NUMBER) {
                    operation.put("value", ((JsonNumber) value).doubleValue());
                }
            }
        }
        
        return operation;
    }

    
    

}
