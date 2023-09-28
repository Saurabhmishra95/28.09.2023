package com.experianhealth.ciam.scimapi.entity;

import java.util.List;

public class PatchOp {
    private List<String> schemas;
    private List<Operation> Operations;

   
    public List<Operation> getOperations() {
        return Operations;
    }

    public void setOperations(List<Operation> operations) {
        this.Operations = operations;
    }

    public List<String> getSchemas() {
        return schemas;
    }

    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }
}

