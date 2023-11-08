package uk.gov.pmrv.api.authorization.rules.services.resource;


import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

@Service
@RequiredArgsConstructor
public class CompAuthAuthorizationResourceService {
    
    private final ResourceScopePermissionService resourceScopePermissionService;
    private final PmrvAuthorizationService pmrvAuthorizationService;

    public boolean hasUserScopeToCompAuth(PmrvUser authUser, Scope scope) {
        Permission requiredPermission = 
                resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.CA, authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .competentAuthority(authUser.getCompetentAuthority())
                    .permission(requiredPermission).build();
        try {
            pmrvAuthorizationService.authorize(authUser, authCriteria);
        } catch (BusinessException e) {
            return false;
        }
        
        return true;
    }
    
    public boolean hasUserScopeOnResourceSubType(PmrvUser authUser, Scope scope, String resourceSubType) {
        Permission requiredPermission = 
				resourceScopePermissionService
						.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.CA,
								resourceSubType, authUser.getRoleType(), scope)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);
        
        AuthorizationCriteria authCriteria = 
                AuthorizationCriteria.builder()
                    .competentAuthority(authUser.getCompetentAuthority())
                    .permission(requiredPermission).build();
        try {
            pmrvAuthorizationService.authorize(authUser, authCriteria);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
}
