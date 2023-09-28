package com.experianhealth.ciam;

import org.apache.commons.lang3.StringUtils;

import java.util.Optional;
import java.util.logging.Logger;

public class EnvironmentSettings {

    public static final String ENV_IDM_BASEURL = "IDM_BASEURL";
    public static final String ENV_AM_BASEURL = "AM_BASEURL";
    public static final String ENV_PORTAL_CLIENT_ID = "PORTAL_CLIENT_ID";
    public static final String ENV_PORTAL_CLIENT_SECRET = "PORTAL_CLIENT_SECRET";
    private static Logger LOGGER = Logger.getLogger(EnvironmentSettings.class.getName());

    private static Optional<String> getEnvironmentSetting(String propertyName) {
        Optional<String> envSetting = Optional.ofNullable(
                StringUtils.stripToNull((System.getProperty(propertyName) != null) ? System.getProperty(propertyName) : System.getenv(propertyName)));
        if(envSetting.isPresent()){
            LOGGER.info("Environment variable " + propertyName + "=" + envSetting.get());
        }else {
            LOGGER.info("Environment variable " + propertyName + " is not set");
        }
        return envSetting;
    }

    private static String getRequiredEnvironmentSetting(String propertyName){
        Optional<String> envSetting = getEnvironmentSetting(propertyName);

        if(envSetting.isEmpty()){
            throw new IllegalStateException("Missing Environment Variable: " + propertyName);
        }
        return envSetting.get();
    }
    public static String getIdmBaseUrl() {
        return getRequiredEnvironmentSetting(ENV_IDM_BASEURL);
    }
    public static String getAmBaseUrl() { 
        return getRequiredEnvironmentSetting(ENV_AM_BASEURL);
    }
    public static String getPortalClientId() {
        return getRequiredEnvironmentSetting(ENV_PORTAL_CLIENT_ID);
    }
    public static String getPortalClientSecret() {
        return getRequiredEnvironmentSetting(ENV_PORTAL_CLIENT_SECRET);
    }
}
