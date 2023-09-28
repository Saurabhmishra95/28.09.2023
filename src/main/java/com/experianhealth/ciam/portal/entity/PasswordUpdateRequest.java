package com.experianhealth.ciam.portal.entity;

public class PasswordUpdateRequest {
    private String currentPassword;
    private String newPassword;

    public PasswordUpdateRequest() {
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
