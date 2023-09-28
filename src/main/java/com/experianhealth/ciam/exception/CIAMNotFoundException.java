package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;

public class CIAMNotFoundException extends CIAMRuntimeException {
    public static final String PUBLIC_DETAIL_MESSAGE_1 = "The requested resource could not be found: ";
    public CIAMNotFoundException(String resource, String exceptionMessage) {
        super(exceptionMessage, PUBLIC_DETAIL_MESSAGE_1 +  resource, HttpStatus.SC_NOT_FOUND);
    }
}
