package com.experianhealth.ciam.exception;


import org.apache.http.HttpStatus;

public class CIAMPasswordException extends CIAMRuntimeException {
    private static final String PUBLIC_DETAIL_MESSAGE = "Password validation failed: ";

    public CIAMPasswordException(String exceptionMessage) {
        super(PUBLIC_DETAIL_MESSAGE + exceptionMessage, exceptionMessage, HttpStatus.SC_BAD_REQUEST);
    }

    public CIAMPasswordException(String resource, String exceptionMessage) {
        super(exceptionMessage, exceptionMessage, HttpStatus.SC_BAD_REQUEST);
    }
}