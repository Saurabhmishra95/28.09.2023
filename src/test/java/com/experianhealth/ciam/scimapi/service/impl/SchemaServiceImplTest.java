package com.experianhealth.ciam.scimapi.service.impl;

import com.experianhealth.ciam.CIAMTestBase;
import com.experianhealth.ciam.RequestContext;
import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.scimapi.entity.Attribute;
import com.experianhealth.ciam.scimapi.entity.Schema;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.entity.SubAttribute;
import com.experianhealth.ciam.scimapi.service.SchemaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SchemaServiceImplTest {


    @Mock
    RequestContextProvider contextProvider;
    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        RequestContext requestContext = mock(RequestContext.class);
        when(requestContext.getBaseUri()).thenReturn(CIAMTestBase.MOCK_SCIM_BASEURL);
        when(contextProvider.getRequestContext()).thenReturn(Optional.of(requestContext));
    }

    @Test
    void testGetSchemas() throws IOException {
        // Create an instance of the SchemaServiceImpl
        SchemaServiceImpl schemaService = new SchemaServiceImpl(contextProvider);

        // Call the mapToSchemas() method
        ScimListResponse<Schema> response = schemaService.getSchemas();

        // Assert that the returned response is not null
        assertNotNull(response);

        List<Schema> schemas = response.getResources();

        // Assert that the returned list is not null
        assertNotNull(schemas);

        // Assert that the list contains two schemas
        assertEquals(2, schemas.size());

        // Assert the properties of the first schema
        Schema userSchema = schemas.get(0);
        assertEquals("urn:ietf:params:scim:schemas:core:2.0:User", userSchema.getId());
        assertEquals("User", userSchema.getName());
        assertEquals("User Account", userSchema.getDescription());

        List<Attribute> userAttributes = userSchema.getAttributes();
        assertNotNull(userAttributes);
        assertEquals(11, userAttributes.size());

        // Assert the properties of the first attribute
        Attribute idAttribute = userAttributes.get(0);
        assertEquals("id", idAttribute.getName());
        assertEquals("string", idAttribute.getType());
        assertEquals("Unique identifier for the User, typically used by the user to directly authenticate to the service provider. Each User MUST include a non-empty userName value. This identifier MUST be unique across the service provider's entire set of Users. REQUIRED.", idAttribute.getDescription());
        assertNull(idAttribute.getSubAttributes());
        // Assert the properties of the "userName" attribute
         Attribute userNameAttribute = userAttributes.get(1);
        assertEquals("userName", userNameAttribute.getName());
        assertEquals("string", userNameAttribute.getType());
        assertEquals("Username of the User.", userNameAttribute.getDescription());
        assertNull(userNameAttribute.getSubAttributes());

        // Assert the properties of the second attribute
        Attribute nameAttribute = userAttributes.get(2);
        assertEquals("name", nameAttribute.getName());
        assertEquals("complex", nameAttribute.getType());
        assertEquals("The components of the user's real name. Providers MAY return just the full name as a single string in the formatted sub-attribute, or they MAY return just the individual component attributes using the other sub-attributes, or they MAY return both. If both variants are returned, they SHOULD be describing the same name, with the formatted name indicating how the component attributes should be combined.", nameAttribute.getDescription());
        assertNotNull(nameAttribute.getSubAttributes());
        assertEquals(3, nameAttribute.getSubAttributes().size());

        // Assert the properties of the first sub-attribute
        SubAttribute givenNameSubAttribute = nameAttribute.getSubAttributes().get(0);
        assertEquals("givenName", givenNameSubAttribute.getName());
        assertEquals("string", givenNameSubAttribute.getType());
        assertEquals("The given name of the User, or first name in most Western languages (e.g., 'Barbara' given the full name 'Ms. Barbara J Jensen, III').", givenNameSubAttribute.getDescription());

        // Assert the properties of the second sub-attribute
        SubAttribute familyNameSubAttribute = nameAttribute.getSubAttributes().get(1);
        assertEquals("familyName", familyNameSubAttribute.getName());
        assertEquals("string", familyNameSubAttribute.getType());
        assertEquals("The family name of the User, or last name in most Western languages (e.g., 'Jensen' given the full name 'Ms. Barbara J Jensen, III').", familyNameSubAttribute.getDescription());

        // Assert the properties of the third sub-attribute
        SubAttribute formattedSubAttribute = nameAttribute.getSubAttributes().get(2);
        assertEquals("formatted", formattedSubAttribute.getName());
        assertEquals("string", formattedSubAttribute.getType());
        assertEquals("The full name, including all middle names, titles, and suffixes as appropriate, formatted for display (e.g., 'Ms. Barbara J Jensen, III').", formattedSubAttribute.getDescription());

        // Assert the properties of the second schema
        Schema groupSchema = schemas.get(1);
        assertEquals("urn:ietf:params:scim:schemas:core:2.0:Groups", groupSchema.getId());
        assertEquals("Group", groupSchema.getName());
        assertEquals("Group Resource", groupSchema.getDescription());

        List<Attribute> groupAttributes = groupSchema.getAttributes();
        assertNotNull(groupAttributes);
        assertEquals(3, groupAttributes.size());
    }

    @Test
    public void testGetSchemaById() {
        // Given
        String schemaId = "urn:ietf:params:scim:schemas:core:2.0:User";
        SchemaService schemaService = new SchemaServiceImpl(contextProvider);
        // When
        Optional<Schema> schemaOptional = schemaService.getSchemaById(schemaId);

        // Then
        assertTrue(schemaOptional.isPresent(), "Schema should be present");
        assertEquals(schemaId, schemaOptional.get().getId(), "Schema ID should match");
    }

    @Test
    public void testGetSchemaById_NotFound() {
        // Given
        String schemaId = "non-existing-schema-id";
        SchemaService schemaService = new SchemaServiceImpl(contextProvider);
        // When
        Optional<Schema> schemaOptional = schemaService.getSchemaById(schemaId);

        // Then
        assertTrue(schemaOptional.isEmpty(), "Schema should not be present");
    }
}