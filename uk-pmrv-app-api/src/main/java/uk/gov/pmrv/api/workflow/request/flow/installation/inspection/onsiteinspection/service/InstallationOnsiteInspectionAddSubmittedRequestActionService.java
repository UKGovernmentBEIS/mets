package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionAddSubmittedRequestActionService;

@Service
public class InstallationOnsiteInspectionAddSubmittedRequestActionService
        extends InstallationInspectionAddSubmittedRequestActionService {

    public InstallationOnsiteInspectionAddSubmittedRequestActionService(RequestService requestService, RequestActionUserInfoResolver requestActionUserInfoResolver) {
        super(requestService, requestActionUserInfoResolver);
    }

    @Override
    protected RequestActionType getRequestActionType() {
        return RequestActionType.INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED;
    }
}
