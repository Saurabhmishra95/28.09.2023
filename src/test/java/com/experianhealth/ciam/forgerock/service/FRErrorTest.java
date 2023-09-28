package com.experianhealth.ciam.forgerock.service;
import com.experianhealth.ciam.forgerock.model.FRError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class FRErrorTest {

    @Test
    public void testFullResponseFRError() throws IOException {

        String json = readResourceFile("forbidden-error-response-more-detail.json");
        //System.out.println(json);
        FRError frError = toObject(json);
        assertNotNull(frError);
        assertEquals(403, frError.getCode());
        assertEquals("Policy validation failed", frError.getMessage());
        assertNotNull(frError.getDetail());
        assertEquals(false, frError.getDetail().getResult());
        assertNotNull(frError.getDetail().getFailedPolicyRequirements());
        assertEquals("userName", frError.getDetail().getFailedPolicyRequirements().get(0).getProperty());
        assertNotNull(frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements());
        assertEquals("VALID_TYPE", frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements().get(0).getPolicyRequirement());
        assertNotNull(frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements().get(0).getParams());
        assertEquals("null", frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements().get(0).getParams().getInvalidType());
        assertEquals("string", frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements().get(0).getParams().getValidTypes().get(0));
        System.out.println(frError);
    }

    @Test
    public void testPartialResponseFRError() throws IOException {

        String json = readResourceFile("forbidden-error-response-some-detail.json");
        //System.out.println(json);
        FRError frError = toObject(json);
        assertNotNull(frError);
        assertEquals(403, frError.getCode());
        assertEquals("Policy validation failed", frError.getMessage());
        assertNotNull(frError.getDetail());
        assertEquals(false, frError.getDetail().getResult());
        assertNotNull(frError.getDetail().getFailedPolicyRequirements());
        assertEquals("userName", frError.getDetail().getFailedPolicyRequirements().get(0).getProperty());
        assertNotNull(frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements());
        assertEquals("VALID_USERNAME", frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements().get(0).getPolicyRequirement());
        assertNull(frError.getDetail().getFailedPolicyRequirements().get(0).getPolicyRequirements().get(0).getParams());
        System.out.println(frError);
    }

    private static FRError toObject(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, FRError.class);
    }
    private static String readResourceFile(String fileName) throws IOException {
        ClassLoader classLoader = FRErrorTest.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(fileName);
        return new String(inputStream.readAllBytes());
    }

    static FRError readDuplicateUsernameError() throws IOException {
        return toObject(readResourceFile("forbidden-error-response-some-detail.json"));
    }
    static FRError readBadUsernameError() throws IOException {
        return toObject(readResourceFile("forbidden-error-response-more-detail.json"));
    }

}
