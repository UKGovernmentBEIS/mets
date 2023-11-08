package uk.gov.pmrv.api.authorization.rules.services.handlers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestNoteAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class RequestNoteAccessRuleHandlerTest {

    @InjectMocks
    private RequestNoteAccessRuleHandler requestNoteAccessRuleHandler;

    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;

    @Mock
    private RequestNoteAuthorityInfoProvider requestNoteAuthorityInfoProvider;

    @Test
    void evaluateRules() {

        final long noteId = 2;
        final long accountId = 1;
        final long verificationBodyId = 3;
        final PmrvUser pmrvUser = PmrvUser.builder().
            roleType(RoleType.REGULATOR)
            .build();
        final AuthorizationRuleScopePermission authorizationRule = AuthorizationRuleScopePermission.builder().build();
        final Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule);
        final AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            .accountId(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .verificationBodyId(verificationBodyId)
            .build();
        final RequestAuthorityInfoDTO requestAuthorityInfoDTO = RequestAuthorityInfoDTO.builder()
            .authorityInfo(ResourceAuthorityInfo.builder()
                .accountId(accountId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(verificationBodyId)
                .build())
            .build();

        when(requestNoteAuthorityInfoProvider.getRequestNoteInfo(noteId)).thenReturn(requestAuthorityInfoDTO);

        requestNoteAccessRuleHandler.evaluateRules(rules, pmrvUser, String.valueOf(noteId));

        verify(requestNoteAuthorityInfoProvider, times(1)).getRequestNoteInfo(noteId);
        verify(pmrvAuthorizationService, times(1)).authorize(pmrvUser, authorizationCriteria);
    }

    @Test
    void evaluateRules_resource_forbidden() {

        final long noteId = 2;
        final long accountId = 1;
        final long verificationBodyId = 3;
        final PmrvUser pmrvUser = PmrvUser.builder().
            roleType(RoleType.OPERATOR)
            .build();
        final AuthorizationRuleScopePermission authorizationRule = AuthorizationRuleScopePermission.builder().build();
        final Set<AuthorizationRuleScopePermission> rules = Set.of(authorizationRule);
        final AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
            .accountId(accountId)
            .competentAuthority(CompetentAuthorityEnum.ENGLAND)
            .verificationBodyId(verificationBodyId)
            .build();
        final RequestAuthorityInfoDTO requestAuthorityInfoDTO = RequestAuthorityInfoDTO.builder()
            .authorityInfo(ResourceAuthorityInfo.builder()
                .accountId(accountId)
                .competentAuthority(CompetentAuthorityEnum.ENGLAND)
                .verificationBodyId(verificationBodyId)
                .build())
            .build();

        when(requestNoteAuthorityInfoProvider.getRequestNoteInfo(noteId)).thenReturn(requestAuthorityInfoDTO);
        doThrow(new BusinessException(ErrorCode.FORBIDDEN)).when(pmrvAuthorizationService)
            .authorize(pmrvUser, authorizationCriteria);

        final BusinessException be = assertThrows(BusinessException.class, () ->
            requestNoteAccessRuleHandler.evaluateRules(rules, pmrvUser, String.valueOf(noteId)));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);

        verify(requestNoteAuthorityInfoProvider, times(1)).getRequestNoteInfo(noteId);
        verify(pmrvAuthorizationService, times(1)).authorize(pmrvUser, authorizationCriteria);
    }
}
