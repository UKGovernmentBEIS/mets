package uk.gov.pmrv.api.authorization.rules.services.resource;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceScopePermission;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.ResourceScopePermissionService;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestTaskAuthorizationResourceService {
    
    private final OperatorAuthorityResourceService operatorAuthorityResourceService;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    private final VerifierAuthorityResourceService verifierAuthorityResourceService;
    private final ResourceScopePermissionService resourceScopePermissionService;
    private final PmrvAuthorizationService pmrvAuthorizationService;
    
    public boolean hasUserExecuteScopeOnRequestTaskType(PmrvUser authUser, String requestTaskType, ResourceCriteria resourceCriteria) {
        Permission requiredPermission = 
                resourceScopePermissionService.findByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, authUser.getRoleType(), Scope.REQUEST_TASK_EXECUTE)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);

        AuthorizationCriteria authCriteria = getAuthorizationCriteriaByRoleType(authUser.getRoleType(), resourceCriteria, requiredPermission);
        try {
            pmrvAuthorizationService.authorize(authUser, authCriteria);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
    
    public boolean hasUserAssignScopeOnRequestTasks(PmrvUser authUser, ResourceCriteria resourceCriteria) {
        Permission requiredPermission = 
                resourceScopePermissionService.findByResourceTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, authUser.getRoleType(), Scope.REQUEST_TASK_ASSIGN)
                .map(ResourceScopePermission::getPermission)
                .orElse(null);

        AuthorizationCriteria authCriteria = getAuthorizationCriteriaByRoleType(authUser.getRoleType(), resourceCriteria, requiredPermission);
        try {
            pmrvAuthorizationService.authorize(authUser, authCriteria);
            return true;
        } catch (BusinessException e) {
            return false;
        }
    }
    
    public List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaAndRoleType(
            String requestTaskType, ResourceCriteria resourceCriteria, RoleType roleType) {
        boolean requiresPermission = 
                resourceScopePermissionService.existsByResourceTypeAndResourceSubTypeAndRoleTypeAndScope(ResourceType.REQUEST_TASK, requestTaskType, roleType, Scope.REQUEST_TASK_EXECUTE);
        
        switch (roleType) {
        case OPERATOR:
            return findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaOperator(requestTaskType, resourceCriteria, requiresPermission);
        case REGULATOR:
            return findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaRegulator(requestTaskType, resourceCriteria, requiresPermission);
        case VERIFIER:
            return findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaVerifier(requestTaskType, resourceCriteria, requiresPermission);
        default:
            throw new UnsupportedOperationException(String.format("Role type %s is not supported yet", roleType));
        }
        
    }

    public Set<String> findRequestTaskTypesByRoleType(RoleType roleType) {
        return resourceScopePermissionService.findByResourceTypeAndRoleType(ResourceType.REQUEST_TASK, roleType).stream()
            .map(ResourceScopePermission::getResourceSubType)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    //Based on PMRV-7560 request-task/request/request-action access for verifiers has become account-based
    private AuthorizationCriteria getAuthorizationCriteriaByRoleType(RoleType roleType, ResourceCriteria resourceCriteria, Permission requiredPermission) {
        return roleType == RoleType.VERIFIER?
                AuthorizationCriteria.builder()
                        .accountId(resourceCriteria.getAccountId())
                        .permission(requiredPermission).build():
                AuthorizationCriteria.builder()
                        .accountId(resourceCriteria.getAccountId())
                        .competentAuthority(resourceCriteria.getCompetentAuthority())
                        .verificationBodyId(resourceCriteria.getVerificationBodyId())
                        .permission(requiredPermission).build();
    }

    private List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaVerifier(String requestTaskType, ResourceCriteria resourceCriteria, boolean requiresPermission) {
        if(!requiresPermission) {
            return verifierAuthorityResourceService.findUsersByVerificationBodyId(resourceCriteria.getVerificationBodyId());
        } else {
            return verifierAuthorityResourceService.
                    findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(
                            ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, resourceCriteria.getVerificationBodyId());
        }
    }

    private List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaRegulator(String requestTaskType, ResourceCriteria resourceCriteria, boolean requiresPermission) {
        if(!requiresPermission) {
            return regulatorAuthorityResourceService.findUsersByCompetentAuthority(resourceCriteria.getCompetentAuthority());
        } else {
            return regulatorAuthorityResourceService.
                    findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                            ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, resourceCriteria.getCompetentAuthority());
        }
    }

    private List<String> findUsersWhoCanExecuteRequestTaskTypeByAccountCriteriaOperator(String requestTaskType,
                                                                                        ResourceCriteria resourceCriteria,
                                                                                        boolean requiresPermission) {
        if(!requiresPermission) {
            return operatorAuthorityResourceService.findUsersByAccountId(resourceCriteria.getAccountId());
        }else {
            return operatorAuthorityResourceService.
                    findUsersWithScopeOnResourceTypeAndSubTypeAndAccountId(
                            ResourceType.REQUEST_TASK, requestTaskType, Scope.REQUEST_TASK_EXECUTE, resourceCriteria.getAccountId());
        }
    }
}
