package uk.gov.pmrv.api.workflow.request.flow.installation.aer.service;

import org.springframework.stereotype.Service;
import uk.gov.netz.api.authorization.core.domain.AppUser;
import uk.gov.netz.api.authorization.rules.domain.ResourceType;
import uk.gov.netz.api.authorization.rules.services.resource.RegulatorAuthorityResourceService;
import uk.gov.netz.api.common.exception.BusinessException;
import uk.gov.netz.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.authorization.rules.domain.PmrvScope;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;

@Service
public class AerRequestService {

    private final RequestRepository requestRepository;
    private final RegulatorAuthorityResourceService regulatorAuthorityResourceService;
    private final WorkflowService workflowService;

    public AerRequestService(RequestRepository requestRepository, RegulatorAuthorityResourceService regulatorAuthorityResourceService, WorkflowService workflowService) {
        this.requestRepository = requestRepository;
        this.regulatorAuthorityResourceService = regulatorAuthorityResourceService;
        this.workflowService = workflowService;
    }

    public boolean canMarkAsNotRequired(String id, AppUser appUser) {
        Request request = requestRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));

        boolean userHasMarkNotRequiredPermission =
        regulatorAuthorityResourceService.
                findUsersWithScopeOnResourceTypeAndSubTypeAndCA(
                        ResourceType.REQUEST, request.getType().name(), PmrvScope.REQUEST_MARK_NOT_REQUIRED, appUser.getCompetentAuthority())
                .contains(appUser.getUserId());

        boolean isAerOfVersionWithApplicableEventAction = workflowService.hasMessageEventSubscriptionWithName(request.getId(),
            BpmnProcessConstants.AER_MARK_NOT_REQUIRED);

        return request.getStatus().equals(RequestStatus.IN_PROGRESS)
                && userHasMarkNotRequiredPermission
                && isAerOfVersionWithApplicableEventAction;
    }
}
