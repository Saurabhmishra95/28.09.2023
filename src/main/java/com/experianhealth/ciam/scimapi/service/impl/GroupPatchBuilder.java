package com.experianhealth.ciam.scimapi.service.impl;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonPatch;
import com.experianhealth.ciam.scimapi.entity.Operation;
import java.util.List;
import java.util.Map;

public class GroupPatchBuilder {
    private final JsonArrayBuilder operations = Json.createArrayBuilder();

    public GroupPatchBuilder applyOperation(Operation operation) {
        String op = operation.getOp().toLowerCase();
        if ("add".equalsIgnoreCase(op) && "members".equalsIgnoreCase(operation.getPath())) {
            Object value = operation.getValue();
            if (value instanceof List) {
                List<Map<String, String>> members = (List<Map<String, String>>) value;
                for (Map<String, String> member : members) {
                    String userId = member.get("value");
                    JsonObjectBuilder operationBuilder = Json.createObjectBuilder()
                            .add("operation", "add")
                            .add("field", "/members/-")
                            .add("value", Json.createObjectBuilder().add("_ref", "managed/user/" + userId));
                    operations.add(operationBuilder);
                }
            } else {
                // Handle error: value field is not a List
                throw new IllegalArgumentException("Invalid operation value: value field is not a List");
            }
        }
        // Handle other operations if necessary
        return this;
    }

    public JsonPatch build() {
        return Json.createPatch(operations.build());
    }
}
