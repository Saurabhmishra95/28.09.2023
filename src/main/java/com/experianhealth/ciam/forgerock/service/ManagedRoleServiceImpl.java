package com.experianhealth.ciam.forgerock.service;

import com.experianhealth.ciam.forgerock.model.FRQuery;
import com.experianhealth.ciam.forgerock.model.Role;
import com.experianhealth.ciam.forgerock.model.RoleMember;
import com.experianhealth.ciam.forgerock.service.helper.GeneralJsonMapper;
import com.experianhealth.ciam.forgerock.service.helper.HTTPRequestProcessor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ManagedRoleServiceImpl extends AbstractForgeRockIDMServiceImpl<Role> implements ManagedRoleService{
    private static final String ROLES_PATH = "/openidm/managed/role";
    private static final String ROLE_MEMBERS_PATH = "/members";
    GeneralJsonMapper<RoleMember> roleMemberMapper;

    ManagedRoleServiceImpl() {
        super(Role.class);
        roleMemberMapper = new GeneralJsonMapper<>(RoleMember.class);
    }

    @Override
    String getBasePath() {
        return ROLES_PATH;
    }

    @Override
    public List<RoleMember> getRoleMembers(String token, String roleId) {
        String roleMembersEndpoint = String.format(getEndpointWithId(roleId) + ROLE_MEMBERS_PATH);
        FRQuery query = FRQuery.queryAll();
        return roleMemberMapper.mapJsonToList(
                HTTPRequestProcessor.sendGetRequest(token, query.buildURI(roleMembersEndpoint))
        );
    }

}
