package com.experianhealth.ciam.forgerock.service.helper;

import com.experianhealth.ciam.exception.CIAMConflictException;
import com.experianhealth.ciam.exception.CIAMForbiddenException;
import com.experianhealth.ciam.exception.CIAMInvalidRequestException;
import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.exception.CIAMPasswordException;
import com.experianhealth.ciam.exception.CIAMRuntimeException;
import com.experianhealth.ciam.exception.CIAMUnauthorizedException;
import com.experianhealth.ciam.forgerock.model.FRError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

public class FRErrorExceptionMapper {

    protected static final Logger LOGGER = Logger.getLogger(FRErrorExceptionMapper.class.getName());

    public static CIAMRuntimeException mapErrorResponse(HttpUriRequest request, CloseableHttpResponse response) {
        int statusCode = response.getStatusLine().getStatusCode();
        CIAMRuntimeException exception;
        if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
            exception = new CIAMUnauthorizedException("Unauthorized request: " + request);

        } else if (statusCode == HttpStatus.SC_FORBIDDEN) {
            exception = mapForbiddenError(request, response);
        } else if (statusCode == HttpStatus.SC_NOT_FOUND) {
            exception = new CIAMNotFoundException(request.getURI().getPath(), "Not Found Request: " + request);
        } else {
            exception = new CIAMRuntimeException("Unexpected error with request: " + request + ", Response: " + response.getStatusLine());
        }
        return exception;
    }

    private static Optional<FRError> toFRError(CloseableHttpResponse response) {
        FRError frError = null;
        try {
            String errorResponse = EntityUtils.toString(response.getEntity(), "UTF-8");
            LOGGER.info("ErrorResponseEntity: " + errorResponse);
            if (StringUtils.isNotEmpty(errorResponse)) {
                frError = new ObjectMapper().readValue(errorResponse, FRError.class);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            LOGGER.info("Unable to parse error response");
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.info("Unable to read error response body");
        }
        return Optional.ofNullable(frError);
    }


    /*
        Note: FR Returns HTTP 403 Errors when other responses should be used, such as HTTP 400, and HTTP 409
        This function tries to untangle that
        */

    private static CIAMRuntimeException mapForbiddenError(HttpUriRequest request, CloseableHttpResponse response) {
        CIAMRuntimeException exception = new CIAMForbiddenException("Forbidden request: " + request);
        Optional<FRError> frErrorOptional = toFRError(response);
        if (frErrorOptional.isEmpty()) return exception;

        FRError frError = frErrorOptional.get();
        if (frError.getDetail() == null) return exception;

        // If there's a detail object, then this Forbidden Error may actually be caused by a failed policy,
        // which is likely either a duplicate record or a missing required attribute. Let's try to find those cases.
        List<FRError.FailedPolicyRequirement> failedPolicyRequirements = frError.getDetail().getFailedPolicyRequirements();

        // Check for failed policy requirements related to "userName"
        Optional<FRError.FailedPolicyRequirement> failedUsernameRequirements = findFailedPolicyRequirement(failedPolicyRequirements, "userName");
        if (failedUsernameRequirements.isPresent()) {
            if (hasPolicyRequirement(failedUsernameRequirements.get().getPolicyRequirements(), "VALID_USERNAME")) {
                exception = new CIAMConflictException(request.getURI().getPath(), "Username already exists: " + request);
            }
            if (hasPolicyRequirement(failedUsernameRequirements.get().getPolicyRequirements(), "VALID_TYPE")) {
                exception = new CIAMInvalidRequestException(request.getURI().getPath(), "Username is invalid or missing: " + request);
            }
        }

        for (FRError.FailedPolicyRequirement failedPolicyRequirement : failedPolicyRequirements) {
            if ("password".equals(failedPolicyRequirement.getProperty())) {
                List<FRError.PolicyRequirement> policyRequirements = failedPolicyRequirement.getPolicyRequirements();

                for (FRError.PolicyRequirement policyRequirement : policyRequirements) {
                    if ("HISTORY".equals(policyRequirement.getPolicyRequirement())) {
                        exception = new CIAMPasswordException(request.getURI().getPath(), "New password cannot be the same as the last two passwords");
                    }
                }
            }
        }

        // Check for failed policy requirements related to "name"
        Optional<FRError.FailedPolicyRequirement> failedNameRequirements = findFailedPolicyRequirement(failedPolicyRequirements, "name");
        if (failedNameRequirements.isPresent()) {
            if (hasPolicyRequirement(failedNameRequirements.get().getPolicyRequirements(), "UNIQUE")) {
                exception = new CIAMConflictException(request.getURI().getPath(), "Name must be unique: " + request);
            }
        }

        return exception;
    }

    private static boolean hasPolicyRequirement(List<FRError.PolicyRequirement> requirements, String requirementName) {
        return requirements.stream().filter(req -> req.getPolicyRequirement().equals(requirementName)).findFirst().isPresent();
    }

    private static Optional<FRError.FailedPolicyRequirement> findFailedPolicyRequirement(List<FRError.FailedPolicyRequirement> failedPolicyRequirements, String propertyName) {
        return failedPolicyRequirements.stream().filter(failedPolicyRequirement -> failedPolicyRequirement.getProperty().equals(propertyName)).findFirst();
    }
}