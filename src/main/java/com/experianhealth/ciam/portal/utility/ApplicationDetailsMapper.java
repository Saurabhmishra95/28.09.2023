package com.experianhealth.ciam.portal.utility;

import org.springframework.stereotype.Component;

import com.experianhealth.ciam.forgerock.model.ApplicationDetails;
import com.experianhealth.ciam.portal.entity.AppDetail;

@Component
public class ApplicationDetailsMapper {
	public AppDetail mapToAppDetail(ApplicationDetails detail) {
	        AppDetail appDetail = new AppDetail();
	        appDetail.setAppId(detail.get_id());
	        appDetail.setAppName(detail.getName());
	        appDetail.setAppDescription(detail.getDescription());
	        appDetail.setAppIcon(detail.getIcon());
	        appDetail.setAppUrl(detail.getUrl());
	        return appDetail;
	    }


}
