package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;


import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;

@Service
@RequiredArgsConstructor
public class NonComplianceCivilPenaltyPeerReviewInitializer implements InitializeRequestTaskHandler {

    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);

    @Override
    public RequestTaskPayload initializePayload(final Request request) {

        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        return NON_COMPLIANCE_MAPPER.toNonComplianceCivilPenaltyRequestTaskPayload(
            requestPayload,
            RequestTaskPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW_PAYLOAD
        );
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW,
                      RequestTaskType.AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW);
    }
}
