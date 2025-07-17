package uk.gov.pmrv.api.workflow.request.flow.installation.inspection.onsiteinspection.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.installation.inspection.common.handler.InstallationInspectionApplicationPeerReviewInitializer;

import java.util.Set;


@Service
@RequiredArgsConstructor
public class InstallationOnsiteInspectionApplicationWaitForPeerReviewInitializer extends InstallationInspectionApplicationPeerReviewInitializer {

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW);
    }

    @Override
    protected RequestTaskPayloadType getRequestTaskPayloadType() {
        return RequestTaskPayloadType.INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW_PAYLOAD;
    }
}
