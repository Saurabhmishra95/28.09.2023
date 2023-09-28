package com.experianhealth.ciam.scimapi.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ScimGroupReference {

    private String value;
    private String $ref;
	private String display;

	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String get$ref() {
		return $ref;
	}
	public void set$ref(String $ref) {
		this.$ref = $ref;
	}

	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
}