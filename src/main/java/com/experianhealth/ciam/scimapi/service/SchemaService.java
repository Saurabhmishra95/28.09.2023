package com.experianhealth.ciam.scimapi.service;


import com.experianhealth.ciam.scimapi.entity.Schema;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;

import java.util.Optional;


public interface SchemaService {
    ScimListResponse<Schema> getSchemas();
    Optional<Schema> getSchemaById(String schemaId);

}
