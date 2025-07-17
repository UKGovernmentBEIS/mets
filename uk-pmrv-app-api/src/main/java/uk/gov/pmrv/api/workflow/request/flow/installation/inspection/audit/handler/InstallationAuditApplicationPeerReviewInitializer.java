package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.audit.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionApplicationPeerReviewInitializer;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class InstallationAuditApplicationPeerReviewInitializer extends InstallationInspectionApplicationPeerReviewInitializer {

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_PAYLOAD;
    }
}
