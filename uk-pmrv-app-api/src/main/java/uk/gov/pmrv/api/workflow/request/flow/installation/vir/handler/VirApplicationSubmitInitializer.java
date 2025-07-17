package uk.gov.pmrv.api.workflow.request.flow.installation.vir.handler;

import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.service.InitializeRequestTaskHandler;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.installation.vir.domain.VirRequestPayload;

import java.util.Set;

@Service
public class VirApplicationSubmitInitializer implements InitializeRequestTaskHandler {

    @Override
    public RequestTaskPayload initializePayload(Request request) {
        final VirRequestPayload requestPayload = (VirRequestPayload) request.getPayload();

        return VirApplicationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.VIR_APPLICATION_SUBMIT_PAYLOAD)
                .verificationData(requestPayload.getVerificationData())
                .build();
    }

    @Override
    public Set<RequestTaskType> getRequestTaskTypes() {
        return Set.of(RequestTaskType.VIR_APPLICATION_SUBMIT);
    }
}
