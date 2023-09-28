package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;

public class CIAMInvalidRequestException extends CIAMRuntimeException {
    public static final String PUBLIC_DETAIL_MESSAGE_1 = "The resource could not be created: ";
    public CIAMInvalidRequestException(String resource, String exceptionMessage) {
        super(exceptionMessage, exceptionMessage, HttpStatus.SC_BAD_REQUEST);
    }
    public CIAMInvalidRequestException(String exceptionMessage) {
        super(exceptionMessage, exceptionMessage, HttpStatus.SC_BAD_REQUEST);
    }
}
