package com.experianhealth.ciam.scimapi.service;



import com.experianhealth.ciam.scimapi.entity.ScimUser;

public interface MeService {
    ScimUser getMe(String token);
}
