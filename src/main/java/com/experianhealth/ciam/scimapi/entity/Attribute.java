package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Attribute {
    private String name;
    private String type;
    private String description;

    private List<SubAttribute> subAttributes;

    // Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public List<SubAttribute> getSubAttributes() {
        return subAttributes;
    }

    public void setSubAttributes(List<SubAttribute> subAttributes) {
        this.subAttributes = subAttributes;
    }

    public void setId(String string) {
        // TODO Auto-generated method stub

    }

}