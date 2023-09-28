package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;

public class CIAMForbiddenException extends CIAMRuntimeException {
    public static final String PUBLIC_DETAIL_MESSAGE = "Authorization failure. The bearer token is unauthorized to perform that action";

    public CIAMForbiddenException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, PUBLIC_DETAIL_MESSAGE, HttpStatus.SC_FORBIDDEN, cause);
    }

    public CIAMForbiddenException(String exceptionMessage) {
        super(exceptionMessage, PUBLIC_DETAIL_MESSAGE, HttpStatus.SC_FORBIDDEN);
    }

}
