package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.RoleMember;

import java.util.List;

import javax.json.JsonPatch;

public interface ManagedRoleService extends GeneralForgeRockIDMService<Role> {
    List<RoleMember> getRoleMembers(String token, String roleId);
 
}
