package com.experianhealth.ciam.portal.entity;

import java.util.List;

public class ApplicationSection {
 private String section;
    public String getSection() {
		return section;
	}
	public void setSection(String section) {
		this.section = section;
	}
	public List<AppDetail> getApps() {
		return apps;
	}
	public void setApps(List<AppDetail> apps) {
		this.apps = apps;
	}
	private List<AppDetail> apps;
}