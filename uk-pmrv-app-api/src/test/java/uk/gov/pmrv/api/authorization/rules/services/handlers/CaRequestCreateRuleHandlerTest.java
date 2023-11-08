package uk.gov.pmrv.api.authorization.rules.services.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvAuthority;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

@ExtendWith(MockitoExtension.class)
class CaRequestCreateRuleHandlerTest {

	@InjectMocks
    private CaRequestCreateRuleHandler cut;
	
	@Mock
    private PmrvAuthorizationService pmrvAuthorizationService;
    
    private final PmrvUser USER = PmrvUser.builder().roleType(RoleType.REGULATOR)
    		.authorities(List.of(PmrvAuthority.builder().competentAuthority(CompetentAuthorityEnum.ENGLAND).build()))
    		.build();
    
    @Test
    void evaluateRules_empty_rules() {
        String resourceId = "1";
        
        BusinessException be = assertThrows(BusinessException.class, () -> cut.evaluateRules(Set.of(), USER,
                resourceId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        verifyNoInteractions(pmrvAuthorizationService);
    }
    
    @Test
    void evaluateRules() {
        AuthorizationRuleScopePermission authorizationRulePermissionScope1 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_PERMIT_BATCH_REISSUE_SUBMIT_EXECUTE_TASK)
            .resourceSubType(RequestCreateActionType.PERMIT_BATCH_REISSUE.name())
            .build();
        AuthorizationRuleScopePermission authorizationRulePermissionScope2 = 
                AuthorizationRuleScopePermission.builder()
            .permission(Permission.PERM_CA_USERS_EDIT)
            .resourceSubType(RequestCreateActionType.PERMIT_BATCH_REISSUE.name())
            .build();
        
        cut.evaluateRules(Set.of(authorizationRulePermissionScope1, authorizationRulePermissionScope2), USER, null);

        verify(pmrvAuthorizationService, times(1)).authorize(USER, AuthorizationCriteria.builder()
        		.permission(Permission.PERM_PERMIT_BATCH_REISSUE_SUBMIT_EXECUTE_TASK)
        		.competentAuthority(CompetentAuthorityEnum.ENGLAND)
        		.build());
        verify(pmrvAuthorizationService, times(1)).authorize(USER, AuthorizationCriteria.builder()
        		.permission(Permission.PERM_CA_USERS_EDIT)
        		.competentAuthority(CompetentAuthorityEnum.ENGLAND)
        		.build());
        verifyNoMoreInteractions(pmrvAuthorizationService);
    }
}
