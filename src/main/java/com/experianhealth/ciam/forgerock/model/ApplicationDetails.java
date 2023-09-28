package com.experianhealth.ciam.forgerock.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApplicationDetails {
	    private String _id;
	    private String _rev;
	    private String description;
	    private List<String> mappingNames;
	    private String url;
	    private String icon;
	    private String name;
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
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public List<String> getMappingNames() {
			return mappingNames;
		}
		public void setMappingNames(List<String> mappingNames) {
			this.mappingNames = mappingNames;
		}
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getIcon() {
			return icon;
		}
		public void setIcon(String icon) {
			this.icon = icon;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
}
