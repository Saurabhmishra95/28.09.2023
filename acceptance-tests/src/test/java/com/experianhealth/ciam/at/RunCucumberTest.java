package com.experianhealth.ciam.at;

import org.junit.platform.suite.api.*;

import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features")
@ConfigurationParameters( {
    @ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty"),
    @ConfigurationParameter(key = "cucumber.publish.quiet", value = "true")
    }
)
public class RunCucumberTest {
}
