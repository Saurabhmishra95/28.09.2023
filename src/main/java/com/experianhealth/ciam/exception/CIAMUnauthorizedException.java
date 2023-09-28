package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;

public class CIAMUnauthorizedException extends CIAMRuntimeException {
    public static final String PUBLIC_DETAIL_MESSAGE = "Missing or invalid authorization token";

    public CIAMUnauthorizedException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, PUBLIC_DETAIL_MESSAGE, HttpStatus.SC_UNAUTHORIZED, cause);
    }

    public CIAMUnauthorizedException(String exceptionMessage) {
        super(exceptionMessage, PUBLIC_DETAIL_MESSAGE, HttpStatus.SC_UNAUTHORIZED);
    }

}
