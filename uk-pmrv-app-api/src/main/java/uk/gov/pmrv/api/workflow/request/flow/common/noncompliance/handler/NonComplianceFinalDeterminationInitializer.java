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

@Service
@RequiredArgsConstructor
public class NonComplianceFinalDeterminationInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return NonComplianceFinalDeterminationRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.NON_COMPLIANCE_FINAL_DETERMINATION_PAYLOAD)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.NON_COMPLIANCE_FINAL_DETERMINATION,
            RequestTaskType.AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION);
    }
}
