package com.experianhealth.ciam;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EnvironmentSettingsTest {

    @Test
    public void testGetIdmBaseUrl_propertyIsSet() {
        String expectedValue = "mockBaseUrl";
        System.setProperty(EnvironmentSettings.ENV_IDM_BASEURL, expectedValue);
        assertEquals(expectedValue, EnvironmentSettings.getIdmBaseUrl());
    }

    @Test
    public void testGetIdmBaseUrl_propertyIsNotSet() {
        System.setProperty(EnvironmentSettings.ENV_IDM_BASEURL, StringUtils.EMPTY);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            EnvironmentSettings.getIdmBaseUrl();
        });
        assertEquals("Missing Environment Variable: " + EnvironmentSettings.ENV_IDM_BASEURL, exception.getMessage());
    }

    @Test
    public void testGetAmBaseUrl_propertyIsSet() {
        String expectedValue = "mockAmBaseUrl";
        System.setProperty(EnvironmentSettings.ENV_AM_BASEURL, expectedValue);
        assertEquals(expectedValue, EnvironmentSettings.getAmBaseUrl());
    }

    @Test
    public void testGetAmBaseUrl_propertyIsNotSet() {
        System.setProperty(EnvironmentSettings.ENV_AM_BASEURL, StringUtils.EMPTY);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            EnvironmentSettings.getAmBaseUrl();
        });
        assertEquals("Missing Environment Variable: " + EnvironmentSettings.ENV_AM_BASEURL, exception.getMessage());
    }

    @Test
    public void testGetPortalClientId_propertyIsSet() {
        String expectedValue = "mockClientId";
        System.setProperty(EnvironmentSettings.ENV_PORTAL_CLIENT_ID, expectedValue);
        assertEquals(expectedValue, EnvironmentSettings.getPortalClientId());
    }

    @Test
    public void testGetPortalClientId_propertyIsNotSet() {
        System.setProperty(EnvironmentSettings.ENV_PORTAL_CLIENT_ID, StringUtils.EMPTY);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            EnvironmentSettings.getPortalClientId();
        });
        assertEquals("Missing Environment Variable: " + EnvironmentSettings.ENV_PORTAL_CLIENT_ID, exception.getMessage());
    }
    @Test
    public void testGetPortalClientSecret_propertyIsSet() {
        String expectedValue = "mockClientSecret";
        System.setProperty(EnvironmentSettings.ENV_PORTAL_CLIENT_SECRET, expectedValue);
        assertEquals(expectedValue, EnvironmentSettings.getPortalClientSecret());
    }

    @Test
    public void testGetPortalCSecret_propertyIsNotSet() {
        System.setProperty(EnvironmentSettings.ENV_PORTAL_CLIENT_SECRET, StringUtils.EMPTY);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            EnvironmentSettings.getPortalClientSecret();
        });
        assertEquals("Missing Environment Variable: " + EnvironmentSettings.ENV_PORTAL_CLIENT_SECRET, exception.getMessage());
    }
}
