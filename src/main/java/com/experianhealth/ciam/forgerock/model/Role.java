package com.experianhealth.ciam.forgerock.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.StringJoiner;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Role {
    private String _id;
    private String name;

    @Override
    public String toString() {
        return new StringJoiner(", ", Role.class.getSimpleName() + "[", "]")
                .add("_id='" + _id + "'")
                .add("name='" + name + "'")
                .add("description='" + description + "'")
                .toString();
    }

    private String description;

    public Role(String _id, String name, String description) {
        this._id = _id;
        this.name = name;
        this.description = description;
    }

    public Role() {

    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
