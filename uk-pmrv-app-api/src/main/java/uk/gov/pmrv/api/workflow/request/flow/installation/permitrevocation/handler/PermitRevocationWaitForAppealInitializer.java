package uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.permitrevocation.domain.PermitRevocationWaitForAppealRequestTaskPayload;

import java.util.Set;

@Service
public class PermitRevocationWaitForAppealInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {

        return PermitRevocationWaitForAppealRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_WAIT_FOR_APPEAL_PAYLOAD)
            .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.PERMIT_REVOCATION_WAIT_FOR_APPEAL);
    }
}
