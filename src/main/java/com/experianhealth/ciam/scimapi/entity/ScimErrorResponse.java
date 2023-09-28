package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public class ScimErrorResponse {
    private static final String SCHEMA = "urn:ietf:params:scim:api:messages:2.0:Error";
    private String[] schemas;
    private String detail;
    private int status;
    private String dateTime;

    public ScimErrorResponse(String[] schemas, String detail, int status) {
        this.schemas = schemas;
        this.detail = detail;
        this.status = status;
    }

    public ScimErrorResponse(String[] schemas, String detail, int status, String dateTime) {
        this.schemas = schemas;
        this.detail = detail;
        this.status = status;
        this.dateTime = dateTime;
    }

    public String[] getSchemas() {
        return schemas;
    }

    public void setSchemas(String[] schemas) {
        this.schemas = schemas;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }


    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }


    public String toJsonString() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    public static String getSchema() {
        return SCHEMA;
    }

}
