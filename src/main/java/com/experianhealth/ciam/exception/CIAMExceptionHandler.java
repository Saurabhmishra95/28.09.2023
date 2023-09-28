package com.experianhealth.ciam.exception;


import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class CIAMExceptionHandler {
    private static final String SCHEMA = "urn:ietf:params:scim:api:messages:2.0:Error";
    private static final String[] SCHEMAS = new String[]{SCHEMA};

    @ExceptionHandler(CIAMRuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleScimException(CIAMRuntimeException e) {
        //TODO use log4j or equivalent
        e.printStackTrace();
        return createResponse(e.getPublicMessage(), e.getHttpStatus());
    }
    
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, Object>> handleScimException(Throwable e) {
        //TODO use log4j or equivalent
        e.printStackTrace();
        return createResponse(CIAMRuntimeException.ERROR_INTERNAL_SERVER, HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<Map<String, Object>> createResponse(String detailMessage, int httpStatus) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("schemas", SCHEMAS);
        errorResponse.put("detail", detailMessage);
        errorResponse.put("status", httpStatus);
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }
}
