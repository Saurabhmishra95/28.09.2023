package com.experianhealth.ciam.forgerock.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Map;
import java.util.StringJoiner;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoleMember {
    private String _id;
    private String _rev;
    private String _ref;
    private String _refResourceCollection;
    private String _refResourceId;
    private Map<String, String> _refProperties;
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String get_rev() {
        return _rev;
    }
    public void set_rev(String _rev) {
        this._rev = _rev;
    }
    public String get_ref() {
        return _ref;
    }
    public void set_ref(String _ref) {
        this._ref = _ref;
    }
    public String get_refResourceCollection() {
        return _refResourceCollection;
    }
    public void set_refResourceCollection(String _refResourceCollection) {
        this._refResourceCollection = _refResourceCollection;
    }
    public String get_refResourceId() {
        return _refResourceId;
    }
    public void set_refResourceId(String _refResourceId) {
        this._refResourceId = _refResourceId;
    }
    public Map<String, String> get_refProperties() {
        return _refProperties;
    }
    public void set_refProperties(Map<String, String> _refProperties) {
        this._refProperties = _refProperties;
    }


    @Override
    public String toString() {
        return new StringJoiner(", ", RoleMember.class.getSimpleName() + "[", "]")
                .add("_id='" + _id + "'")
                .add("_rev='" + _rev + "'")
                .add("_ref='" + _ref + "'")
                .add("_refResourceCollection='" + _refResourceCollection + "'")
                .add("_refResourceId='" + _refResourceId + "'")
                .add("_refProperties=" + _refProperties)
                .toString();
    }
}
