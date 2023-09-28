package com.experianhealth.ciam;

import org.junit.jupiter.api.BeforeAll;

public class CIAMTestBase {
    public static final String MOCK_IDM_BASEURL = "http://mock-fg-base-url";
    public static String MOCK_SCIM_BASEURL = "http://mock-scim-base-url";
    public static String MOCK_AM_BASEURL = "http://mock-am-base-url";

    public static String MOCK_CLIENT_ID = "mockPortalClientId";
    public static String MOCK_CLIENT_SECRET = "mockPortalClientSecret";

    @BeforeAll
    public static void beforeAll() {
        System.setProperty(EnvironmentSettings.ENV_IDM_BASEURL, MOCK_IDM_BASEURL);
        System.setProperty(EnvironmentSettings.ENV_AM_BASEURL, MOCK_AM_BASEURL);
        System.setProperty(EnvironmentSettings.ENV_PORTAL_CLIENT_ID, MOCK_CLIENT_ID);
        System.setProperty(EnvironmentSettings.ENV_PORTAL_CLIENT_SECRET, MOCK_CLIENT_SECRET);
    }
}
