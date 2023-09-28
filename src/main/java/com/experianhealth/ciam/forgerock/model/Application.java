package com.experianhealth.ciam.forgerock.model;

import java.util.List;

public class Application {
    private String name;
    private String _id;
    private String _rev;
    private String _refResourceCollection;
    private String _refResourceId;
    private String _ref;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String get_ref() {
        return _ref;
    }

    public void set_ref(String _ref) {
        this._ref = _ref;
    }


}
