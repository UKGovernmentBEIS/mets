package uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.handler;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceFinalDeterminationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.noncompliance.domain.NonComplianceRequestPayload;

@Service
@RequiredArgsConstructor
public class NonComplianceFinalDeterminationInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        NonComplianceRequestPayload requestPayload = (NonComplianceRequestPayload) request.getPayload();

        return NonComplianceFinalDeterminationRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_FINAL_DETERMINATION_PAYLOAD)
                .nonComplianceComments(requestPayload.getComments())
                .nonComplianceDate(requestPayload.getNonComplianceDate())
                .complianceDate(requestPayload.getComplianceDate())
                .reason(requestPayload.getReason())
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NON_COMPLIANCE_FINAL_DETERMINATION,
            RequestTaskType.AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION);
    }
}
