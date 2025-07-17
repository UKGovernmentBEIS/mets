package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.service;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.service.InstallationInspectionAddSubmittedRequestActionService;

@Service
public class InstallationAuditAddSubmittedRequestActionService
        extends InstallationInspectionAddSubmittedRequestActionService {

    public InstallationAuditAddSubmittedRequestActionService(RequestService requestService, RequestActionUserInfoResolver requestActionUserInfoResolver) {
        super(requestService, requestActionUserInfoResolver);
    }

    @Override
    protected RequestActionType getRequestActionType() {
        return RequestActionType.INSTALLATION_AUDIT_APPLICATION_SUBMITTED;
    }
}