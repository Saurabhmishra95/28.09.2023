package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;


public class CIAMBadRequestException extends CIAMRuntimeException {
    private static final String CUSTOM_BAD_REQUEST_MESSAGE = "Bad Request ";

    public CIAMBadRequestException(String exceptionMessage) {
        super(exceptionMessage, CUSTOM_BAD_REQUEST_MESSAGE , HttpStatus.SC_BAD_REQUEST);
    }

    public CIAMBadRequestException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, CUSTOM_BAD_REQUEST_MESSAGE, HttpStatus.SC_BAD_REQUEST, cause);
    }
}
