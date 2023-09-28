package com.experianhealth.ciam.scimapi.service.impl;


import com.experianhealth.ciam.EnvironmentSettings;
import com.experianhealth.ciam.RequestContextProvider;
import com.experianhealth.ciam.scimapi.controller.ScimEndpoints;
import com.experianhealth.ciam.scimapi.entity.Attribute;
import com.experianhealth.ciam.scimapi.entity.Schema;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.entity.SubAttribute;
import com.experianhealth.ciam.scimapi.service.SchemaService;
import com.experianhealth.ciam.scimapi.entity.Meta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;

@Service
public class SchemaServiceImpl implements SchemaService {

    private final RequestContextProvider contextProvider;

    // Constructor to initialize the schemas list
    @Autowired
    public SchemaServiceImpl(RequestContextProvider contextProvider) {
        this.contextProvider = contextProvider;
    }

    // Method to return all the schemas
    @Override
    public ScimListResponse<Schema> getSchemas() {
        List<Schema> schemas = getSchemaList();
        ScimListResponse<Schema> response = new ScimListResponse<>();
        response.setSchemas(Collections.singletonList("urn:ietf:params:scim:api:messages:2.0:ListResponse"));
        response.setTotalResults(schemas.size());
        response.setStartIndex(1);
        response.setItemsPerPage(schemas.size());
        response.setResources(schemas);
        return response;
    }

    private List<Schema> getSchemaList() {
        Schema userSchema = createUserSchema();
        Schema groupSchema = createGroupSchema();
        return Arrays.asList(userSchema, groupSchema);
    }

    // Method to return a schema by its ID
    @Override
    public Optional<Schema> getSchemaById(String schemaId) {
        return getSchemaList().stream()
                .filter(schema -> schema.getId().equals(schemaId))
                .findFirst();
    }


    // Method to create the User schema
    private Schema createUserSchema() {
        Schema schema = new Schema();
        schema.setId("urn:ietf:params:scim:schemas:core:2.0:User");
        schema.setName("User");
        schema.setDescription("User Account");

        List<Attribute> attributes = Arrays.asList(
                createAttribute("id", "string", "Unique identifier for the User, typically used by the user to directly authenticate to the service provider. Each User MUST include a non-empty userName value. This identifier MUST be unique across the service provider's entire set of Users. REQUIRED."),
                createAttribute("userName", "string", "Username of the User."),
                createComplexAttribute("name", "The components of the user's real name. Providers MAY return just the full name as a single string in the formatted sub-attribute, or they MAY return just the individual component attributes using the other sub-attributes, or they MAY return both. If both variants are returned, they SHOULD be describing the same name, with the formatted name indicating how the component attributes should be combined.", createNameSubAttributes()),
                createAttribute("displayName", "string", "The name of the User, suitable for display to end-users. The name SHOULD be the full name of the User being described if known."),
                createComplexAttribute("address", "A physical mailing address for the User Canonical type values of 'work', 'home', and 'other'. This attribute is a complex type with the following sub-attributes.", createAddressSubAttributes()),
                createComplexAttribute("emails", "The email addresses of the User.", createEmailSubAttributes()),
                createComplexAttribute("phoneNumbers", "The phone numbers of the User.",createphoneNumbersAttributes()),
                createAttribute("active", "boolean", "Indicates whether the User is active or not."),
                createAttribute("locale", "string", "The locale of the User."),
                createAttribute("timezone", "string", "The timezone of the User."),
                createComplexAttribute("groups", "The groups to which the User belongs.", createGroupsSubAttributes())
        );
        Meta meta = createMeta("Schema", "urn:ietf:params:scim:schemas:core:2.0:User");

        schema.setAttributes(attributes);
        schema.setMeta(meta);

        return schema;
    }

    // Method to create the Group schema
    private Schema createGroupSchema() {
        Schema schema = new Schema();
        schema.setId("urn:ietf:params:scim:schemas:core:2.0:Groups");
        schema.setName("Group");
        schema.setDescription("Group Resource");

        List<Attribute> attributes = Arrays.asList(
                createAttribute("id", "string", "Identifier for the group"),
                createAttribute("displayName", "string", "A human-readable name for the group"),
                createAttribute("description", "string", "A description of the group")
        );
        Meta meta = createMeta("Schema", "urn:ietf:params:scim:schemas:core:2.0:Group");
        schema.setAttributes(attributes);
        schema.setMeta(meta);

        return schema;
    }

    // Method to create an attribute
    private Attribute createAttribute(String name, String type, String description) {
        Attribute attribute = new Attribute();
        attribute.setName(name);
        attribute.setType(type);
        attribute.setDescription(description);
        return attribute;
    }

    // Method to create a complex attribute
    private Attribute createComplexAttribute(String name, String description, List<SubAttribute> subAttributes) {
        Attribute attribute = createAttribute(name, "complex", description);
        attribute.setSubAttributes(subAttributes);
        return attribute;
    }

    // Method to create the sub-attributes for the 'name' attribute
    private List<SubAttribute> createNameSubAttributes() {
        return Arrays.asList(
                createSubAttribute("givenName", "string", "The given name of the User, or first name in most Western languages (e.g., 'Barbara' given the full name 'Ms. Barbara J Jensen, III')."),
                createSubAttribute("familyName", "string", "The family name of the User, or last name in most Western languages (e.g., 'Jensen' given the full name 'Ms. Barbara J Jensen, III')."),
                createSubAttribute("formatted", "string", "The full name, including all middle names, titles, and suffixes as appropriate, formatted for display (e.g., 'Ms. Barbara J Jensen, III').")

        );
    }

    // Method to create the sub-attributes for the 'address' attribute
    private List<SubAttribute> createAddressSubAttributes() {
        return Arrays.asList(

                createSubAttribute("primary", "boolean", "Indicates whether the address is the primary address of the User"),
                createSubAttribute("locality", "string", "The locality of the User's address"),
                createSubAttribute("region", "string", "The region of the User's address"),
                createSubAttribute("postalCode", "string", "The postal code of the User's address"),
                createSubAttribute("country", "string", "The country of the User's address"),
                createSubAttribute("formatted", "string", "The formatted representation of the User's address")
        );
    }

    // Method to create the sub-attributes for the 'emails' attribute
    private List<SubAttribute> createphoneNumbersAttributes() {
        return Arrays.asList(

                createSubAttribute("value", "string", "Phone number of the User."),
                createSubAttribute("type", "string", "A label indicating the attribute function, e.g., 'work', 'home', 'mobile'")
        );
    }

    private List<SubAttribute> createEmailSubAttributes() {
        return Arrays.asList(

                createSubAttribute("primary", "boolean", "Indicates whether the email address is the primary address of the User."),
                createSubAttribute("value", "string", "The work email address value"),
                createSubAttribute("type", "string", "The personal email of the email address.")
        );
    }

    private List<SubAttribute> createGroupsSubAttributes() {
        return Arrays.asList(
                createSubAttribute("value", "string", "The identifier of the User's group."),
                createSubAttribute("$ref", "reference", "The URI of the corresponding 'Group'resource to which the user belongs.")

        );
    }


    // Method to create a sub-attribute
    private SubAttribute createSubAttribute(String name, String type, String description) {
        SubAttribute subAttribute = new SubAttribute();
        subAttribute.setName(name);
        subAttribute.setType(type);
        subAttribute.setDescription(description);
        return subAttribute;
    }

    // Method to create "meta" information
    private Meta createMeta(String resourceType, String schemaId) {
        Meta meta = new Meta();
        meta.setResourceType(resourceType);

        String scimBaseUrl = contextProvider.getRequestContext().get().getBaseUri();
        String location = scimBaseUrl + ScimEndpoints.SCHEMAS + "/" + schemaId;
        meta.setLocation(location);
        return meta;
    }


}
