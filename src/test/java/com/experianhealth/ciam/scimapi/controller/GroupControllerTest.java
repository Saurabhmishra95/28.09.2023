package com.experianhealth.ciam.scimapi.controller;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.scimapi.entity.ScimGroup;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.service.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupControllerTest extends CIAMTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private GroupService groupService;

    @InjectMocks
    private GroupController groupController;

    @BeforeEach
    public  void beforeEach() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(groupController).build();
    }
    @Test
    public void testGetGroups() throws Exception {
        ScimGroup scimGroup = new ScimGroup();
        scimGroup.setId("testId");
        scimGroup.setDisplayName("testName");
        scimGroup.setDescription("testDescription");

        List<ScimGroup> scimGroups = Arrays.asList(scimGroup);
        String bearerToken = "someToken";

        // Create ScimListResponse and set your groups to it
        ScimListResponse<ScimGroup> scimListResponse = new ScimListResponse<>();
        scimListResponse.setResources(scimGroups);

        // Return the ScimListResponse from your service mock
        when(groupService.getGroups(bearerToken)).thenReturn(scimListResponse);

        mockMvc.perform(get("/scim/v2/Groups")
                        .header("Authorization", bearerToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}

