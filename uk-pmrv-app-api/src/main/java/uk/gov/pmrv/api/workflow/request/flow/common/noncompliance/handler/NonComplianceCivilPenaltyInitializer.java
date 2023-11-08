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
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceCivilPenaltyRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.mapper.NonComplianceMapper;

@Service
@RequiredArgsConstructor
public class NonComplianceCivilPenaltyInitializer implements InitializeRequestTaskHandler {

    private static final NonComplianceMapper NON_COMPLIANCE_MAPPER = Mappers.getMapper(NonComplianceMapper.class);

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        final NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();
        // when returning from a peer review the data should be retained, whereas 
        // when returning from a reissue civil penalty notice determination the data should be cleared
        final boolean reIssueCivilPenalty = requestPayload.isReIssueCivilPenalty();
        final RequestTaskPayload taskPayload = reIssueCivilPenalty ?
            NonComplianceCivilPenaltyRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD)
                .build() :
            NON_COMPLIANCE_MAPPER.toNonComplianceCivilPenaltyRequestTaskPayload(
                requestPayload,
                RequestTaskPayloadType.NON_COMPLIANCE_CIVIL_PENALTY_PAYLOAD
            );
        
        requestPayload.setReIssueCivilPenalty(false);
        return taskPayload;
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NON_COMPLIANCE_CIVIL_PENALTY, RequestTaskType.AVIATION_NON_COMPLIANCE_CIVIL_PENALTY);
    }
}
