package com.experianhealth.ciam.exception;

import org.apache.http.HttpStatus;

public class CIAMRuntimeException extends RuntimeException {

    public static final String ERROR_RETRIEVING_GROUPS = "Error retrieving groups";
    public static final String ERROR_RESPONSE_EMPTY = "Response is empty";
    public static final String ERROR_PARSING_RESPONSE = "Error parsing response";
    public static final String ERROR_INTERNAL_SERVER = "Internal Server Error";
    public static final String ERROR_RETRIEVING_USERS_MESSAGE = "Error retrieving users";
    int httpStatus = HttpStatus.SC_INTERNAL_SERVER_ERROR;
    String publicMessage = ERROR_INTERNAL_SERVER;


    public CIAMRuntimeException(String exceptionMessage) {
        super(exceptionMessage);
    }

    public CIAMRuntimeException(String exceptionMessage, Throwable cause) {
        super(exceptionMessage, cause);
    }

    CIAMRuntimeException(String exceptionMessage, String publicMessage) {
        super(exceptionMessage);
        this.publicMessage = publicMessage;
    }

    public CIAMRuntimeException(String exceptionMessage, String publicMessage, int httpStatus) {
        super(exceptionMessage);
        this.publicMessage = publicMessage;
        this.httpStatus = httpStatus;
    }

    public CIAMRuntimeException(String exceptionMessage, String publicMessage, int httpStatus, Throwable cause) {
        this(exceptionMessage, publicMessage);
        this.publicMessage = publicMessage;
        this.httpStatus = httpStatus;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getPublicMessage() {
        return publicMessage;
    }
}
