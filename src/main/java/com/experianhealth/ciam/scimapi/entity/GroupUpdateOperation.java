package com.experianhealth.ciam.scimapi.entity;

import java.util.Map;

public class GroupUpdateOperation extends Operation {
    private Map<String, Object> value;

    @Override
    public Map<String, Object> getValue() {
        return value;
    }

    @Override
    public void setValue(Object value) {
        this.value = (Map<String, Object>) value;
    }
}
