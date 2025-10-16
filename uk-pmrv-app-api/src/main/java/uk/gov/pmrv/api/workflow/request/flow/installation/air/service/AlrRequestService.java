package uk.gov.pmrv.api.workflow.request.flow.installation.air.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.authorization.rules.domain.PmrvScope;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

@Service
@RequiredArgsConstructor
public class AlrRequestService {

    private final RequestRepository requestRepository;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;

    public boolean userCanMarkAlrAsNotRequired(String requestId, AppUser appUser) {
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        boolean userHasMarkNotRequiredPermission = regulatorAuthorityResourceService
                .findUsersWithScopeOnResourceTypeAndSubTypeAndCA(ResourceType.REQUEST,request.getType().name(),
                        PmrvScope.REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority()).contains(appUser.getUserId());

        return request.getStatus().equals(RequestStatus.IN_PROGRESS)
                && userHasMarkNotRequiredPermission;
    }

}
