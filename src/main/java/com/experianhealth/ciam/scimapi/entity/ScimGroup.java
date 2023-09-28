package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScimGroup {


    private List<String> schemas ;
    private String id;
    private String displayName;
    private String description;

    public List<GroupMember> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMember> members) {
        this.members = members;
    }

    private List<GroupMember> members;

    public ScimGroup(List<String> schemas, String id, String displayName, String description) {
        this.schemas = schemas;
        this.id = id;
        this.displayName = displayName;
        this.description = description;
    }

    public ScimGroup() {

    }




    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = (id != null) ? id : "";
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = (displayName != null) ? displayName : "";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = (description != null) ? description : "";
    }

    public List<String> getSchemas() {
        return schemas;
    }
    public void setSchemas(List<String> schemas) {
        this.schemas = schemas;
    }
}
