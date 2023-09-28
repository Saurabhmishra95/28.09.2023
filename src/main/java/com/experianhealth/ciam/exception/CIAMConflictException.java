package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;

public class CIAMConflictException extends CIAMRuntimeException {
    public static final String PUBLIC_DETAIL_MESSAGE_1 = "The resource could not be created: ";
    public CIAMConflictException(String resource, String exceptionMessage) {
        super(exceptionMessage, PUBLIC_DETAIL_MESSAGE_1 +  resource, HttpStatus.SC_CONFLICT);
    }
}
