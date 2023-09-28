package com.experianhealth.ciam.forgerock.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FRError {
    private int code;
    private String reason;
    private String message;
    Detail detail;


    // Getter Methods

    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }

    public String getMessage() {
        return message;
    }

    public Detail getDetail() {
        return detail;
    }

    // Setter Methods

    public void setCode(int code) {
        this.code = code;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setDetail(Detail detailObject) {
        this.detail = detailObject;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", FRError.class.getSimpleName() + "[", "]")
                .add("code=" + code)
                .add("reason='" + reason + "'")
                .add("message='" + message + "'")
                .add("detail=" + detail)
                .toString();
    }

    static private boolean hasPolicyRequirement( List<FRError.PolicyRequirement> requirements, String requirementName) {
        return requirements.stream().filter(req -> req.getPolicyRequirement().equals(requirementName)).findFirst().isPresent();
    }
    static private Optional<FailedPolicyRequirement> findFailedPolicyRequirement(List<FRError.FailedPolicyRequirement> failedPolicyRequirements, String propertyName){
        return failedPolicyRequirements.stream().filter(failedPolicyRequirement -> failedPolicyRequirement.getProperty().equals(propertyName)).findFirst();
    }

    public static class Detail {
        private boolean result;
        private List<FailedPolicyRequirement> failedPolicyRequirements;

        public boolean getResult() {
            return result;
        }

        public void setResult(boolean result) {
            this.result = result;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Detail.class.getSimpleName() + "[", "]")
                    .add("result=" + result)
                    .add("failedPolicyRequirements=" + failedPolicyRequirements)
                    .toString();
        }

        public void setFailedPolicyRequirements(List<FailedPolicyRequirement> failedPolicyRequirements) {
            this.failedPolicyRequirements = failedPolicyRequirements;
        }

        public boolean isResult() {
            return result;
        }

        public List<FailedPolicyRequirement> getFailedPolicyRequirements() {
            return failedPolicyRequirements;
        }
    }

    public static class FailedPolicyRequirement {
        private String property;
        private List<PolicyRequirement> policyRequirements;

        @Override
        public String toString() {
            return new StringJoiner(", ", FailedPolicyRequirement.class.getSimpleName() + "[", "]")
                    .add("property='" + property + "'")
                    .add("policyRequirements=" + policyRequirements)
                    .toString();
        }

        public void setProperty(String property) {
            this.property = property;
        }

        public void setPolicyRequirements(List<PolicyRequirement> policyRequirements) {
            this.policyRequirements = policyRequirements;
        }

        public String getProperty() {
            return property;
        }

        public List<PolicyRequirement> getPolicyRequirements() {
            return policyRequirements;
        }
    }

    public static class PolicyRequirement {
        private String policyRequirement;
        private Params params;

        @Override
        public String toString() {
            return new StringJoiner(", ", PolicyRequirement.class.getSimpleName() + "[", "]")
                    .add("policyRequirement='" + policyRequirement + "'")
                    .add("params=" + params)
                    .toString();
        }

        public String getPolicyRequirement() {
            return policyRequirement;
        }

        public void setPolicyRequirement(String policyRequirement) {
            this.policyRequirement = policyRequirement;
        }

        public Params getParams() {
            return params;
        }

        public void setParams(Params params) {
            this.params = params;
        }
    }
    public static class Params {
        String invalidType;
        List<String> validTypes;

        int historyCount; // Updated field name
        int historyDurationSeconds; // Updated field name

        @Override
        public String toString() {
            return new StringJoiner(", ", Params.class.getSimpleName() + "[", "]")
                    .add("invalidType='" + invalidType + "'")
                    .add("validTypes=" + validTypes)
                    .add("historyCount=" + historyCount) // Updated field name
                    .add("historyDurationSeconds=" + historyDurationSeconds) // Updated field name
                    .toString();
        }

        public String getInvalidType() {
            return invalidType;
        }

        public void setInvalidType(String invalidType) {
            this.invalidType = invalidType;
        }

        public List<String> getValidTypes() {
            return validTypes;
        }

        public void setValidTypes(List<String> validTypes) {
            this.validTypes = validTypes;
        }
        @JsonProperty("history-duration-seconds")
        public int getHistoryDurationSeconds() {
            return historyDurationSeconds;
        }

        public void setHistoryDurationSeconds(int historyDurationSeconds) {
            this.historyDurationSeconds = historyDurationSeconds;
        }
        @JsonProperty("history-count")
        public int getHistoryCount() {
            return historyCount;
        }

        public void setHistoryCount(int historyCount) {
            this.historyCount = historyCount;
        }
    }



}

