package uk.gov.pmrv.api.authorization.rules.services.handlers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.core.domain.PmrvUser;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationResourceRuleHandler;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestTaskAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.competentauthority.CompetentAuthorityEnum;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//Based on PMRV-7560 request-task/request/request-action access for verifiers has become account-based
@Service("requestTaskAccountBasedAccessHandler")
@RequiredArgsConstructor
public class RequestTaskAccountBasedAccessRuleHandler implements AuthorizationResourceRuleHandler {
    private final PmrvAuthorizationService pmrvAuthorizationService;
    private final RequestTaskAuthorityInfoProvider requestTaskAuthorityInfoProvider;

    /**
     * @param user the authenticated user
     * @param authorizationRules the list of
     * @param resourceId the resourceId for which the rules apply.
     * @throws BusinessException {@link ErrorCode} FORBIDDEN if authorization fails.
     *
     * Authorizes access on {@link uk.gov.pmrv.api.account.domain.Account} or {@link CompetentAuthorityEnum},
     * the {@link uk.gov.pmrv.api.workflow.request.core.domain.Request} of {@link uk.gov.pmrv.api.workflow.request.core.domain.RequestTask} with id the {@code resourceId}
     * and permission the {@link uk.gov.pmrv.api.authorization.core.domain.Permission} of the rule
     */
    @Override
    public void evaluateRules(@Valid Set<AuthorizationRuleScopePermission> authorizationRules, PmrvUser user, String resourceId) {
        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = requestTaskAuthorityInfoProvider.getRequestTaskInfo(Long.parseLong(resourceId));

        List<AuthorizationRuleScopePermission> filteredRules = authorizationRules.stream()
                .filter(rule -> requestTaskInfoDTO.getType().equals(rule.getResourceSubType()))
                .collect(Collectors.toList());

        if("SYSTEM_MESSAGE_NOTIFICATION".equals(requestTaskInfoDTO.getRequestType())) {
            if (!requestTaskInfoDTO.getAssignee().equals(user.getUserId())){
                throw new BusinessException(ErrorCode.FORBIDDEN);
            }
            //As long as System messages are personal and cannot be reassigned a user should always have access to it.
            else return;
        }

        if (filteredRules.isEmpty()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }

        filteredRules.forEach(rule -> {
            AuthorizationCriteria authorizationCriteria = AuthorizationCriteria.builder()
                    .accountId(requestTaskInfoDTO.getAuthorityInfo().getAccountId())
                    .permission(rule.getPermission()).build();
            pmrvAuthorizationService.authorize(user, authorizationCriteria);
        });
    }
}
