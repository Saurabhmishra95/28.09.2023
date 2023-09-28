package com.experianhealth.ciam.scimapi.controller;

import com.experianhealth.ciam.exception.CIAMNotFoundException;
import com.experianhealth.ciam.scimapi.entity.Schema;
import com.experianhealth.ciam.scimapi.entity.ScimListResponse;
import com.experianhealth.ciam.scimapi.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;


@RestController
@RequestMapping(ScimEndpoints.SCHEMAS)
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @GetMapping
    public ScimListResponse<Schema> mapToSchemas() {
        return schemaService.getSchemas();
    }

    @GetMapping("/{schemaId}")
    public ResponseEntity<Schema> getSchemaById(@PathVariable String schemaId) {
        Optional<Schema> schema = schemaService.getSchemaById(schemaId);
        if(schema.isEmpty()){
            throw new CIAMNotFoundException(ScimEndpoints.SCHEMAS + "/" + schemaId, "Schema not found");
        }
        return ResponseEntity.ok(schema.get());
    }


}

